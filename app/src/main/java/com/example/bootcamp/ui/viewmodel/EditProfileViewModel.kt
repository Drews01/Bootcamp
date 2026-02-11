package com.example.bootcamp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.domain.model.UserProfile
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingKtp: Boolean = false,
    val profile: UserProfile? = null,

    // Form fields
    val address: String = "",
    val nik: String = "",
    val phoneNumber: String = "",
    val accountNumber: String = "",
    val bankName: String = "",
    val ktpUri: Uri? = null,
    // Default to empty string instead of null to match Request object requirements if needed, or keep null logic
    val ktpPath: String = "",

    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userProfileRepository.getUserProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            address = profile.address ?: "",
                            nik = profile.nik ?: "",
                            phoneNumber = profile.phoneNumber ?: "",
                            accountNumber = profile.accountNumber ?: "",
                            bankName = profile.bankName ?: "",
                            ktpPath = profile.ktpPath ?: ""
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
        }
    }

    fun onAddressChanged(value: String) {
        if (value.length <= 250) {
            _uiState.update { it.copy(address = value) }
        }
    }

    fun onNikChanged(value: String) {
        if (value.length <= 16 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(nik = value) }
        }
    }

    fun onPhoneNumberChanged(value: String) {
        if (value.length <= 15 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(phoneNumber = value) }
        }
    }

    fun onAccountNumberChanged(value: String) {
        if (value.length <= 16 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(accountNumber = value) }
        }
    }

    fun onBankNameChanged(value: String) {
        if (value.length <= 50) {
            _uiState.update { it.copy(bankName = value) }
        }
    }

    fun onKtpFileSelected(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isUploadingKtp = true, errorMessage = null) }

            val compressedFile = ImageUtils.compressImage(context, uri)

            if (compressedFile != null) {
                val compressedUri = Uri.fromFile(compressedFile)
                // Update URI for preview with the compressed one (optional, but good for consistency)
                _uiState.update { it.copy(ktpUri = compressedUri) }
                uploadKtp(compressedUri)
            } else {
                _uiState.update {
                    it.copy(
                        isUploadingKtp = false,
                        errorMessage = "Failed to process image"
                    )
                }
            }
        }
    }

    private fun isValidImageSize(uri: Uri): Boolean {
        // Validation is now handled by compression, or we can keep it as a pre-check if needed.
        // For now, let's rely on compression to fix the size.
        return true
    }

    private fun uploadKtp(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingKtp = true, errorMessage = null) }

            userProfileRepository.uploadKtp(uri)
                .onSuccess { path ->
                    if (!path.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                isUploadingKtp = false,
                                ktpPath = path,
                                successMessage = "KTP uploaded successfully"
                            )
                        }
                    } else {
                        // Path missing in response but upload succeeded.
                        // Reload profile to fetch the updated path from server.
                        _uiState.update { it.copy(isUploadingKtp = false) }
                        loadProfile()
                    }
                }
                .onFailure { exception ->
                    android.util.Log.e("EditProfileVM", "Upload failed", exception)
                    _uiState.update {
                        it.copy(
                            isUploadingKtp = false,
                            errorMessage = "Failed to upload KTP: ${exception.message}",
                            ktpUri = null
                        )
                    }
                }
        }
    }

    fun saveProfile() {
        val state = _uiState.value

        // Input Sanitization
        val address = state.address.trim()
        val nik = state.nik.trim()
        val phoneNumber = state.phoneNumber.trim()
        val accountNumber = state.accountNumber.trim()
        val bankName = state.bankName.trim()

        // Validation
        if (address.isBlank() ||
            nik.isBlank() ||
            phoneNumber.isBlank() ||
            accountNumber.isBlank() ||
            bankName.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields") }
            return
        }

        if (nik.length != 16) {
            _uiState.update { it.copy(errorMessage = "NIK must be 16 digits") }
            return
        }

        if (state.ktpPath.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please upload your KTP photo") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val request = UserProfileRequest(
                address = address,
                nik = nik,
                phoneNumber = phoneNumber,
                accountNumber = accountNumber,
                bankName = bankName,
                ktpPath = state.ktpPath
            )

            userProfileRepository.submitProfile(request)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Profile updated successfully"
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = exception.message)
                    }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
