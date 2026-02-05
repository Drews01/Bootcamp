package com.example.bootcamp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.model.LoanMilestone
import com.example.bootcamp.domain.usecase.loan.GetLoanHistoryUseCase
import com.example.bootcamp.domain.usecase.loan.GetLoanMilestonesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val getLoanHistoryUseCase: GetLoanHistoryUseCase,
    private val getLoanMilestonesUseCase: GetLoanMilestonesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanHistoryUiState())
    val uiState: StateFlow<LoanHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getLoanHistoryUseCase()
                .onSuccess { loans ->
                    _uiState.update { it.copy(isLoading = false, loans = loans) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
        }
    }

    fun toggleLoanExpansion(loanId: Long) {
        val currentExpanded = _uiState.value.expandedLoanId
        if (currentExpanded == loanId) {
            // Collapse
            _uiState.update { it.copy(expandedLoanId = null, milestoneError = null) }
        } else {
            // Expand and fetch milestones if not already loaded
            _uiState.update { it.copy(expandedLoanId = loanId, milestoneError = null) }
            if (!_uiState.value.milestones.containsKey(loanId)) {
                loadMilestones(loanId)
            }
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
}
