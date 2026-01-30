package com.example.bootcamp.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootcamp.util.Language
import com.example.bootcamp.util.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Language Settings
 */
data class LanguageUiState(
    val currentLanguage: Language = Language.ENGLISH,
    val availableLanguages: List<Language> = Language.entries,
    val isChanging: Boolean = false
)

/**
 * ViewModel for managing language settings UI.
 */
@HiltViewModel
class LanguageViewModel @Inject constructor(private val languageManager: LanguageManager) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        // Observe language changes from LanguageManager
        viewModelScope.launch {
            languageManager.currentLanguage.collect { language ->
                _uiState.value = _uiState.value.copy(currentLanguage = language)
            }
        }
    }

    /**
     * Change the app language.
     * @param language The new language to set
     * @param activity The current activity (needed for recreation)
     */
    fun changeLanguage(language: Language, activity: Activity?) {
        // Allow re-selecting the same language to force a refresh/sync if needed
        // if (language == _uiState.value.currentLanguage) return

        _uiState.value = _uiState.value.copy(isChanging = true)
        languageManager.setLanguage(language, activity)
        // Note: Activity will be recreated, so isChanging will reset automatically
    }
}
