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

/**
 * Sealed class representing different types of loan submission errors.
 * Each type has specific UI handling requirements.
 */
sealed class LoanErrorType {
    /** User profile is incomplete - redirect to Edit Profile */
    object IncompleteProfile : LoanErrorType()
    
    /** User already has an active loan */
    object ActiveLoanExists : LoanErrorType()
    
    /** Loan amount exceeds credit limit */
    data class ExceedsLimit(val remainingLimit: String, val tier: String) : LoanErrorType()
    
    /** Branch not selected */
    object BranchRequired : LoanErrorType()
    
    /** Branch not found - refresh list */
    object BranchNotFound : LoanErrorType()
    
    /** System error - no tier available */
    object NoTierAvailable : LoanErrorType()
    
    /** Generic error */
    data class Generic(val message: String) : LoanErrorType()
}

/** UI State for the Loan Submission screen. */
data class LoanUiState(
        val branches: List<Branch> = emptyList(),
        val isBranchesLoading: Boolean = false,
        val selectedBranch: Branch? = null,
        val amount: String = "",
        val tenure: String = "",
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null,
        val errorType: LoanErrorType? = null,
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
        private val submitLoanUseCase: SubmitLoanUseCase,
        private val loanRepository: com.example.bootcamp.domain.repository.LoanRepository
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
            _uiState.update { 
                it.copy(
                    errorMessage = "Please fill all fields correctly",
                    errorType = if (branchId == null) LoanErrorType.BranchRequired else LoanErrorType.Generic("Please fill all fields correctly")
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true, errorMessage = null, errorType = null, successMessage = null)
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
                                    errorType = null,
                                    amount = "",
                                    tenure = "",
                                    selectedBranch = null
                            )
                        }
                    }
                    .onFailure { exception ->
                        val errorType = parseErrorType(exception)
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = if (errorType is LoanErrorType.Generic) exception.message ?: "Submission failed" else null,
                                errorType = errorType
                            )
                        }
                    }
        }
    }

    /**
     * Parse the error exception and return the appropriate LoanErrorType.
     */
    private fun parseErrorType(exception: Throwable): LoanErrorType {
        android.util.Log.d("LoanViewModel", "parseErrorType called with: $exception")
        android.util.Log.d("LoanViewModel", "Exception class: ${exception::class.simpleName}")
        
        // Check for specific error codes from backend (extract errorCode from ApiException)
        if (exception is com.example.bootcamp.data.remote.base.ApiException) {
            val errorCode = exception.getErrorCode()
            android.util.Log.d("LoanViewModel", "ApiException detected. errorCode=$errorCode, statusCode=${exception.statusCode}")
            android.util.Log.d("LoanViewModel", "ErrorDetails: ${exception.errorDetails}")
            when (errorCode) {
                com.example.bootcamp.data.remote.base.ErrorCode.PROFILE_INCOMPLETE -> {
                    android.util.Log.d("LoanViewModel", "Matched ErrorCode: PROFILE_INCOMPLETE")
                    return LoanErrorType.IncompleteProfile
                }
                com.example.bootcamp.data.remote.base.ErrorCode.ACTIVE_LOAN_EXISTS -> {
                    android.util.Log.d("LoanViewModel", "Matched ErrorCode: ACTIVE_LOAN_EXISTS")
                    return LoanErrorType.ActiveLoanExists
                }
                com.example.bootcamp.data.remote.base.ErrorCode.CREDIT_LIMIT_EXCEEDED -> {
                    android.util.Log.d("LoanViewModel", "Matched ErrorCode: CREDIT_LIMIT_EXCEEDED")
                    val errorMessage = exception.message ?: ""
                    val tierRegex = Regex("for (\\w+) tier", RegexOption.IGNORE_CASE)
                    val tier = tierRegex.find(errorMessage)?.groupValues?.get(1) ?: "your"
                    fetchCreditInfo(tier)
                    return LoanErrorType.ExceedsLimit(remainingLimit = "checking...", tier = tier)
                }
                com.example.bootcamp.data.remote.base.ErrorCode.BRANCH_REQUIRED -> {
                    android.util.Log.d("LoanViewModel", "Matched ErrorCode: BRANCH_REQUIRED")
                    return LoanErrorType.BranchRequired
                }
                com.example.bootcamp.data.remote.base.ErrorCode.BRANCH_NOT_FOUND -> {
                    android.util.Log.d("LoanViewModel", "Matched ErrorCode: BRANCH_NOT_FOUND")
                    return LoanErrorType.BranchNotFound
                }
                com.example.bootcamp.data.remote.base.ErrorCode.NO_TIER_AVAILABLE -> {
                    android.util.Log.d("LoanViewModel", "Matched ErrorCode: NO_TIER_AVAILABLE")
                    return LoanErrorType.NoTierAvailable
                }
                else -> { /* unknown errorCode: fall through to message parsing */ }
            }
        }

        // Fallback to message parsing
        val errorMessage = exception.message ?: ""
        android.util.Log.d("LoanViewModel", "Fallback parsing for message: $errorMessage")
        
        return when {
            errorMessage.contains("profile is incomplete", ignoreCase = true) -> {
                android.util.Log.d("LoanViewModel", "Matched message: profile is incomplete")
                LoanErrorType.IncompleteProfile
            }
            
            errorMessage.contains("active loan", ignoreCase = true) -> {
                android.util.Log.d("LoanViewModel", "Matched message: active loan")
                LoanErrorType.ActiveLoanExists
            }
            
            errorMessage.contains("exceeds remaining credit limit", ignoreCase = true) -> {
                android.util.Log.d("LoanViewModel", "Matched message: exceeds credit limit")
                val tierRegex = Regex("for (\\w+) tier", RegexOption.IGNORE_CASE)
                val tier = tierRegex.find(errorMessage)?.groupValues?.get(1) ?: "your"
                fetchCreditInfo(tier)
                LoanErrorType.ExceedsLimit(remainingLimit = "checking...", tier = tier)
            }
            
            errorMessage.contains("Branch ID is required", ignoreCase = true) -> {
                android.util.Log.d("LoanViewModel", "Matched message: Branch ID required")
                LoanErrorType.BranchRequired
            }
            
            errorMessage.contains("Branch not found", ignoreCase = true) -> {
                android.util.Log.d("LoanViewModel", "Matched message: Branch not found")
                LoanErrorType.BranchNotFound
            }
            
            errorMessage.contains("No tier product available", ignoreCase = true) -> {
                android.util.Log.d("LoanViewModel", "Matched message: No tier product")
                LoanErrorType.NoTierAvailable
            }
            
            else -> {
                android.util.Log.d("LoanViewModel", "No match found, valid generic error")
                LoanErrorType.Generic(errorMessage)
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null, errorType = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    private fun fetchCreditInfo(tierName: String) {
        viewModelScope.launch {
            loanRepository.getUserAvailableCredit()
                .onSuccess { limit ->
                    // Update the error state with the real limit
                    val formattedLimit = java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(limit)
                    _uiState.update { state ->
                        if (state.errorType is LoanErrorType.ExceedsLimit) {
                            state.copy(
                                errorType = LoanErrorType.ExceedsLimit(remainingLimit = formattedLimit, tier = tierName)
                            )
                        } else {
                            state
                        }
                    }
                }
        }
    }
}
