package com.example.bootcamp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.domain.model.ProfileUpdate
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI State for the Edit Profile Form screen. */
data class EditProfileFormUiState(
    val email: String = "",
    val address: String = "",
    val nik: String = "",
    val ktpPath: String = "",
    val phoneNumber: String = "",
    val accountNumber: String = "",
    val bankName: String = "",
    val selectedImageUri: Uri? = null,
    val isUploading: Boolean = false,
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val imageValidationError: String? = null
) {
    val isFormValid: Boolean
        get() =
            address.isNotBlank() &&
                nik.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                accountNumber.isNotBlank() &&
                bankName.isNotBlank() &&
                ktpPath.isNotBlank()

    val isSubmitEnabled: Boolean
        get() = isFormValid && !isSubmitting && !isUploading
}

/** Allowed MIME types for KTP upload. */
private val ALLOWED_IMAGE_TYPES = listOf("image/jpeg", "image/png", "image/jpg")

@HiltViewModel
class EditProfileFormViewModel
@Inject
constructor(
    private val userProfileRepository: UserProfileRepository,
    private val authRepository: AuthRepository,
    private val sessionRepository: com.example.bootcamp.domain.repository.SessionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileFormUiState())
    val uiState: StateFlow<EditProfileFormUiState> = _uiState.asStateFlow()

    init {
        loadEmail()
    }

    private fun loadEmail() {
        viewModelScope.launch {
            sessionRepository.getEmailFlow().first()?.let { email ->
                _uiState.update { it.copy(email = email) }
            }
        }
    }

    fun onAddressChanged(value: String) {
        _uiState.update { it.copy(address = value) }
    }

    fun onNikChanged(value: String) {
        _uiState.update { it.copy(nik = value) }
    }

    fun onPhoneNumberChanged(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun onAccountNumberChanged(value: String) {
        _uiState.update { it.copy(accountNumber = value) }
    }

    fun onBankNameChanged(value: String) {
        _uiState.update { it.copy(bankName = value) }
    }

    /** Handle image selection. Validates MIME type before accepting. */
    fun onImageSelected(uri: Uri?) {
        if (uri == null) {
            _uiState.update { it.copy(imageValidationError = "No image selected") }
            return
        }

        val mimeType = context.contentResolver.getType(uri)
        if (mimeType == null || mimeType !in ALLOWED_IMAGE_TYPES) {
            _uiState.update {
                it.copy(
                    imageValidationError =
                    "Invalid image type. Only JPEG, PNG, and JPG are allowed.",
                    selectedImageUri = null
                )
            }
            return
        }

        _uiState.update { it.copy(selectedImageUri = uri, imageValidationError = null) }

        // Auto-upload the image
        uploadKtp(uri)
    }

    private fun uploadKtp(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null) }

            userProfileRepository
                .uploadKtp(uri)
                .onSuccess { ktpPath ->
                    _uiState.update { it.copy(isUploading = false, ktpPath = ktpPath) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            errorMessage = "Failed to upload KTP: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun submitProfile() {
        val state = _uiState.value
        if (!state.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val update =
                ProfileUpdate(
                    address = state.address,
                    nik = state.nik,
                    ktpPath = state.ktpPath,
                    phoneNumber = state.phoneNumber,
                    accountNumber = state.accountNumber,
                    bankName = state.bankName
                )

            userProfileRepository
                .submitProfile(update)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            successMessage = "Profile saved successfully!"
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "Failed to save profile: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearImageValidationError() {
        _uiState.update { it.copy(imageValidationError = null) }
    }
}
