package com.example.bootcamp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.data.remote.dto.UserTierLimitDTO
import com.example.bootcamp.data.repository.ProductRepository
import com.example.bootcamp.domain.model.UserProfile
import com.example.bootcamp.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val userTier: UserTierLimitDTO? = null,
    val userProfile: UserProfile? = null,
    val error: String? = null,
    val isProfileLoading: Boolean = false,
    val isTierLoading: Boolean = false
) {
    // Helper to check if we should show the "Unlock Limit" empty state
    // Show if profile exists (or maybe we show it even if profile doesn't exist? Requirement says: "Profile matches 'New User' / 404 -> Show 'Complete Your Profile'")
    // Actually, prompt says:
    // Case 1: Tier Data Exists -> Show Product Tier Card
    // Case 2: Profile matches "New User" / 404 -> Show "Complete Your Profile" / CTA (Empty State Card)

    // So if userTier is null, we show EmptyStateCard.
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadData() {
        fetchUserTier()
        fetchUserProfile()
    }

    private fun fetchUserTier() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTierLoading = true) }
            // Assuming fetchUserTier returns Result<UserTierLimitDTO?>
            productRepository.fetchUserTier()
                .onSuccess { tier ->
                    _uiState.update { it.copy(userTier = tier, isTierLoading = false) }
                }
                .onFailure { e ->
                    // If 404 or other error, strictly we might want to treat it as "no tier" depending on backend.
                    // But repository handles empty body as success(null).
                    // If real error, maybe log it. For now, just set loading false.
                    _uiState.update { it.copy(isTierLoading = false, error = e.message) }
                }
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProfileLoading = true) }
            userProfileRepository.getUserProfile()
                .onSuccess { profile ->
                    _uiState.update { it.copy(userProfile = profile, isProfileLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isProfileLoading = false) }
                    // If 404, userProfile remains null
                }
        }
    }
}
