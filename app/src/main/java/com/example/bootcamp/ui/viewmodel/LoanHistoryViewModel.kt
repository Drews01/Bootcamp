package com.example.bootcamp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.model.LoanMilestone
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.usecase.loan.GetLoanMilestonesUseCase
import com.example.bootcamp.domain.usecase.loan.ObserveLoanHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanHistoryUiState(
    val isLoading: Boolean = false,
    val loans: List<LoanApplication> = emptyList(),
    val errorMessage: String? = null,
    val expandedLoanId: Long? = null,
    val milestonesLoading: Boolean = false,
    val milestones: Map<Long, List<LoanMilestone>> = emptyMap(),
    val milestoneError: String? = null
)

@HiltViewModel
class LoanHistoryViewModel @Inject constructor(
    private val observeLoanHistoryUseCase: ObserveLoanHistoryUseCase,
    private val loanRepository: LoanRepository,
    private val getLoanMilestonesUseCase: GetLoanMilestonesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanHistoryUiState())
    val uiState: StateFlow<LoanHistoryUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        observeHistory()
        startPolling()
    }

    /** Observe local cache via Room Flow — emits whenever DB changes. */
    private fun observeHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            observeLoanHistoryUseCase()
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
                .collect { loans ->
                    val previousLoans = _uiState.value.loans
                    _uiState.update { it.copy(isLoading = false, loans = loans) }

                    // Auto-refresh milestones if an expanded loan's status changed
                    val expandedId = _uiState.value.expandedLoanId
                    if (expandedId != null && previousLoans.isNotEmpty()) {
                        val oldLoan = previousLoans.find { it.id == expandedId }
                        val newLoan = loans.find { it.id == expandedId }
                        if (oldLoan != null && newLoan != null && oldLoan.status != newLoan.status) {
                            loadMilestones(expandedId)
                        }
                    }
                }
        }
    }

    /** Periodically fetch fresh data from the remote API and update local cache. */
    private fun startPolling() {
        pollingJob = viewModelScope.launch {
            // Initial fetch
            loanRepository.refreshLoanHistory()

            // Periodic refresh
            while (isActive) {
                delay(POLLING_INTERVAL_MS)
                loanRepository.refreshLoanHistory()
                // Errors during polling are silently ignored;
                // the last successful data remains visible via Room Flow.
            }
        }
    }

    /** Manual refresh (e.g., retry button). Triggers an immediate remote fetch. */
    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            loanRepository.refreshLoanHistory()
                .onFailure { exception ->
                    // Only show error if we have no cached data
                    if (_uiState.value.loans.isEmpty()) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = exception.message)
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun toggleLoanExpansion(loanId: Long) {
        val currentExpanded = _uiState.value.expandedLoanId
        if (currentExpanded == loanId) {
            // Collapse
            _uiState.update { it.copy(expandedLoanId = null, milestoneError = null) }
        } else {
            // Expand and fetch milestones
            _uiState.update { it.copy(expandedLoanId = loanId, milestoneError = null) }
            loadMilestones(loanId)
        }
    }

    private fun loadMilestones(loanId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(milestonesLoading = true, milestoneError = null) }

            getLoanMilestonesUseCase(loanId)
                .onSuccess { milestones ->
                    _uiState.update { state ->
                        state.copy(
                            milestonesLoading = false,
                            milestones = state.milestones + (loanId to milestones)
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(milestonesLoading = false, milestoneError = exception.message)
                    }
                }
        }
    }

    companion object {
        /** Interval between remote data refreshes in milliseconds. */
        const val POLLING_INTERVAL_MS = 15_000L
    }
}
