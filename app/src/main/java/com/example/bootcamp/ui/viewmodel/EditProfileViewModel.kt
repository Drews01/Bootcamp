package com.example.bootcamp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.domain.model.UserProfile
import com.example.bootcamp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: UserProfile? = null,
    
    // Form fields
    val address: String = "",
    val nik: String = "",
    val phoneNumber: String = "",
    val accountNumber: String = "",
    val bankName: String = "",
    val ktpUri: Uri? = null,
    val ktpPath: String? = null,
    
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.getUserProfile()
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
                            ktpPath = profile.ktpPath
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

    fun onKtpSelected(uri: Uri) {
        _uiState.update { it.copy(ktpUri = uri) }
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

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            authRepository.updateProfile(
                address = state.address,
                nik = state.nik,
                phoneNumber = state.phoneNumber,
                accountNumber = state.accountNumber,
                bankName = state.bankName
            )
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
