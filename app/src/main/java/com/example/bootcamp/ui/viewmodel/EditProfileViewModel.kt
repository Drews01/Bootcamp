package com.example.bootcamp.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.domain.model.UserProfile
import com.example.bootcamp.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val ktpPath: String = "", // Default to empty string instead of null to match Request object requirements if needed, or keep null logic
    
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
        _uiState.update { it.copy(address = value) }
    }

    fun onNikChanged(value: String) {
        if (value.length <= 16 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(nik = value) }
        }
    }

    fun onPhoneNumberChanged(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(phoneNumber = value) }
        }
    }

    fun onAccountNumberChanged(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(accountNumber = value) }
        }
    }

    fun onBankNameChanged(value: String) {
        _uiState.update { it.copy(bankName = value) }
    }

    fun onKtpFileSelected(uri: Uri) {
        // Validate image size (e.g. max 2MB)
        if (!isValidImageSize(uri)) {
            _uiState.update { it.copy(errorMessage = "Image size is too large. Max 2MB allowed.") }
            return
        }
        
        // Update URI for preview
        _uiState.update { it.copy(ktpUri = uri) }
        
        // Auto upload
        uploadKtp(uri)
    }
    
    private fun isValidImageSize(uri: Uri): Boolean {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        val size = it.getLong(sizeIndex)
                        return size <= 2 * 1024 * 1024 // 2MB
                    }
                }
            }
            true // Unable to check size, assume valid
        } catch (e: Exception) {
            true
        }
    }

    private fun uploadKtp(uri: Uri) {
         viewModelScope.launch {
            _uiState.update { it.copy(isUploadingKtp = true, errorMessage = null) }
            
            userProfileRepository.uploadKtp(uri)
                .onSuccess { path ->
                    _uiState.update { 
                        it.copy(
                            isUploadingKtp = false,
                            ktpPath = path,
                            successMessage = "KTP uploaded successfully"
                        ) 
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isUploadingKtp = false, 
                            errorMessage = "Failed to upload KTP: ${exception.message}",
                            ktpUri = null // Revert preview on failure
                        ) 
                    }
                }
         }
    }

    fun saveProfile() {
        val state = _uiState.value
        
        // Validation
        if (state.address.isBlank() || state.nik.isBlank() || 
            state.phoneNumber.isBlank() || state.accountNumber.isBlank() || 
            state.bankName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields") }
            return
        }

        if (state.nik.length != 16) {
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
                address = state.address,
                nik = state.nik,
                phoneNumber = state.phoneNumber,
                accountNumber = state.accountNumber,
                bankName = state.bankName,
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
