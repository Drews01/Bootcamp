package com.example.bootcamp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.data.remote.base.ApiException
import com.example.bootcamp.domain.model.UserProfile
import com.example.bootcamp.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileDetailsUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val profileNotFound: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileDetailsViewModel @Inject constructor(private val userProfileRepository: UserProfileRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(ProfileDetailsUiState())
    val uiState: StateFlow<ProfileDetailsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, profileNotFound = false)
            }

            userProfileRepository
                .getUserProfile()
                .onSuccess { profile ->
                    _uiState.update { it.copy(isLoading = false, profile = profile) }
                }
                .onFailure { exception ->
                    // Check if it's a 404 Not Found error
                    val is404 = (exception as? ApiException)?.statusCode == 404

                    if (is404) {
                        _uiState.update { it.copy(isLoading = false, profileNotFound = true) }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = exception.message)
                        }
                    }
                }
        }
    }
}
