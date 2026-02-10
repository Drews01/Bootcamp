package com.example.bootcamp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.data.remote.base.ApiException
import com.example.bootcamp.data.remote.base.ErrorDetails
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.service.FCMService
import com.example.bootcamp.domain.usecase.auth.ForgotPasswordParams
import com.example.bootcamp.domain.usecase.auth.ForgotPasswordUseCase
import com.example.bootcamp.domain.usecase.auth.GoogleLoginParams
import com.example.bootcamp.domain.usecase.auth.GoogleLoginUseCase
import com.example.bootcamp.domain.usecase.auth.LoginParams
import com.example.bootcamp.domain.usecase.auth.LoginUseCase
import com.example.bootcamp.domain.usecase.auth.LogoutUseCase
import com.example.bootcamp.domain.usecase.auth.RegisterParams
import com.example.bootcamp.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for authentication screens. Includes field-specific errors for form validation. */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val userId: String? = null,
    val email: String? = null,

    // Messages
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Field-specific errors (for form validation)
    val fieldErrors: Map<String, String> = emptyMap(),

    // Error details for advanced handling
    // Error details for advanced handling
    val errorDetails: ErrorDetails? = null,

    // Pending Data Status (for logout warning)
    val hasPendingData: Boolean = false
) {
    /** Get error for a specific field. */
    fun getFieldError(fieldName: String): String? = fieldErrors[fieldName]

    /** Check if a specific field has an error. */
    fun hasFieldError(fieldName: String): Boolean = fieldErrors.containsKey(fieldName)

    /** Check if there are any field errors. */
    fun hasAnyFieldErrors(): Boolean = fieldErrors.isNotEmpty()

    /** Check if this is a validation error. */
    fun isValidationError(): Boolean = errorDetails?.isValidationError() == true
}

/**
 * ViewModel managing authentication state and operations. Uses Hilt for dependency injection and
 * UseCases for business logic. Supports field-specific error display for form validation.
 */
@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,

    private val authRepository: AuthRepository,
    private val userProfileRepository: com.example.bootcamp.domain.repository.UserProfileRepository,
    private val loanRepository: com.example.bootcamp.domain.repository.LoanRepository,
    private val fcmService: FCMService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getTokenFlow().collect { token ->
                _uiState.update { it.copy(isLoggedIn = token != null) }
            }
        }
        viewModelScope.launch {
            authRepository.getUsernameFlow().collect { username ->
                _uiState.update { it.copy(username = username) }
            }
        }
        viewModelScope.launch {
            authRepository.getUserIdFlow().collect { userId ->
                _uiState.update { it.copy(userId = userId) }
            }
        }
        viewModelScope.launch {
            authRepository.getEmailFlow().collect { email ->
                _uiState.update { it.copy(email = email) }
            }
        }

        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                userProfileRepository.getPendingProfile(),
                loanRepository.getPendingLoans()
            ) { pendingProfile, pendingLoans ->
                (pendingProfile != null) || (pendingLoans.isNotEmpty())
            }.collect { hasPending ->
                _uiState.update { it.copy(hasPendingData = hasPending) }
            }
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String = password) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null,
                    fieldErrors = emptyMap(),
                    errorDetails = null
                )
            }

            registerUseCase(RegisterParams(username, email, password, confirmPassword))
                .onSuccess {
                    // Auto-login after successful registration
                    login(username, password)
                }
                .onFailure { exception -> handleError(exception) }
        }
    }

    fun handleGoogleLogin(idToken: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null,
                    fieldErrors = emptyMap(),
                    errorDetails = null
                )
            }

            // Fetch FCM token for push notification registration
            val fcmToken = fcmService.getToken()

            val deviceName = android.os.Build.MODEL
            val platform = "ANDROID"

            googleLoginUseCase(
                GoogleLoginParams(
                    idToken = idToken,
                    fcmToken = fcmToken,
                    deviceName = deviceName,
                    platform = platform
                )
            )
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = message,
                        )
                    }
                }
                .onFailure { exception -> handleError(exception) }
        }
    }

    fun login(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null,
                    fieldErrors = emptyMap(),
                    errorDetails = null
                )
            }

            // Fetch FCM token for push notification registration
            val fcmToken = fcmService.getToken()

            val deviceName = android.os.Build.MODEL
            val platform = "ANDROID"

            loginUseCase(LoginParams(usernameOrEmail, password, fcmToken, deviceName, platform))
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = message,
                        )
                    }
                }
                .onFailure { exception -> handleError(exception) }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null,
                    fieldErrors = emptyMap(),
                    errorDetails = null
                )
            }

            forgotPasswordUseCase(ForgotPasswordParams(email))
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = message,
                        )
                    }
                }
                .onFailure { exception -> handleError(exception) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            logoutUseCase()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = "Logged out successfully",
                )
            }
        }
    }

    /**
     * Handle errors from API calls. Extracts field errors from ApiException for form validation.
     */
    private fun handleError(exception: Throwable) {
        val errorDetails = (exception as? ApiException)?.errorDetails
        val fieldErrors = errorDetails?.fieldErrors ?: emptyMap()
        val errorMessage =
            when {
                fieldErrors.isNotEmpty() -> errorDetails?.getDisplayMessage()
                else -> exception.message
            }
                ?: "An error occurred"

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = errorMessage,
                fieldErrors = fieldErrors,
                errorDetails = errorDetails
            )
        }
    }

    /** Clear a specific field error. Call this when user starts editing a field. */
    fun clearFieldError(fieldName: String) {
        _uiState.update { it.copy(fieldErrors = it.fieldErrors - fieldName) }
    }

    /** Clear all messages and errors. */
    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null,
                fieldErrors = emptyMap(),
                errorDetails = null
            )
        }
    }

    /** Clear only success message. */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    /** Clear only error message (but keep field errors). */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
