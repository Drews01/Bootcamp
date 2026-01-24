package com.example.bootcamp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.usecase.loan.GetBranchesUseCase
import com.example.bootcamp.domain.usecase.loan.SubmitLoanParams
import com.example.bootcamp.domain.usecase.loan.SubmitLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** UI State for the Loan Submission screen. */
data class LoanUiState(
        val branches: List<Branch> = emptyList(),
        val isBranchesLoading: Boolean = false,
        val selectedBranch: Branch? = null,
        val amount: String = "",
        val tenure: String = "",
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null
) {
    val isSubmitEnabled: Boolean
        get() =
                amount.isNotBlank() &&
                        tenure.isNotBlank() &&
                        selectedBranch != null &&
                        !isSubmitting
}

/** ViewModel for managing loan submission logic and state. */
@HiltViewModel
class LoanViewModel
@Inject
constructor(
        private val getBranchesUseCase: GetBranchesUseCase,
        private val submitLoanUseCase: SubmitLoanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    init {
        loadBranches()
    }

    fun loadBranches() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBranchesLoading = true, errorMessage = null) }

            getBranchesUseCase()
                    .onSuccess { branches ->
                        _uiState.update { it.copy(isBranchesLoading = false, branches = branches) }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                    isBranchesLoading = false,
                                    errorMessage = "Failed to load branches: ${exception.message}"
                            )
                        }
                    }
        }
    }

    fun onBranchSelected(branch: Branch) {
        _uiState.update { it.copy(selectedBranch = branch) }
    }

    fun onAmountChanged(amount: String) {
        if (amount.all { it.isDigit() }) {
            _uiState.update { it.copy(amount = amount) }
        }
    }

    fun onTenureChanged(tenure: String) {
        if (tenure.all { it.isDigit() }) {
            _uiState.update { it.copy(tenure = tenure) }
        }
    }

    fun submitLoan() {
        val currentState = _uiState.value
        val amountLong = currentState.amount.toLongOrNull()
        val tenureInt = currentState.tenure.toIntOrNull()
        val branchId = currentState.selectedBranch?.id

        if (amountLong == null || tenureInt == null || branchId == null) {
            _uiState.update { it.copy(errorMessage = "Please fill all fields correctly") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true, errorMessage = null, successMessage = null)
            }

            submitLoanUseCase(
                            SubmitLoanParams(
                                    amount = amountLong,
                                    tenureMonths = tenureInt,
                                    branchId = branchId
                            )
                    )
                    .onSuccess { message ->
                        _uiState.update {
                            it.copy(
                                    isSubmitting = false,
                                    successMessage = message,
                                    amount = "",
                                    tenure = "",
                                    selectedBranch = null
                            )
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                    isSubmitting = false,
                                    errorMessage = exception.message ?: "Submission failed"
                            )
                        }
                    }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }


}
