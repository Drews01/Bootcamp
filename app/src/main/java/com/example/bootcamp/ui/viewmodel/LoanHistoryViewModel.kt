package com.example.bootcamp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.usecase.loan.GetLoanHistoryUseCase
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
    val errorMessage: String? = null
)

@HiltViewModel
class LoanHistoryViewModel @Inject constructor(private val getLoanHistoryUseCase: GetLoanHistoryUseCase) : ViewModel() {

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
}
