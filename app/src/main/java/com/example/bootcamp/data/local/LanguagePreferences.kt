package com.example.bootcamp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bootcamp.util.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "language_preferences"
)

/**
 * DataStore-based preferences for storing language selection.
 * Default language is Indonesian.
 */
@Singleton
class LanguagePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.languageDataStore

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
    }

    /**
     * Flow of the currently selected language.
     * Emits updates whenever the language preference changes.
     * Defaults to INDONESIAN.
     */
    val selectedLanguage: Flow<Language> = dataStore.data.map { preferences ->
        val code = preferences[LANGUAGE_KEY] ?: Language.INDONESIAN.code
        Language.fromCode(code)
    }

    /**
     * Save the selected language to DataStore.
     */
    suspend fun setLanguage(language: Language) {
        // Save to DataStore
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }

        // Also save to SharedPreferences for synchronous retrieval (getLanguageSync)
        val sharedPrefs = context.getSharedPreferences(
            "language_preferences",
            Context.MODE_PRIVATE
        )
        sharedPrefs.edit().putString("selected_language", language.code).apply()
    }

    /**
     * Synchronous getter for initial app startup.
     * Uses SharedPreferences as a fallback for synchronous access.
     * Defaults to INDONESIAN.
     */
    fun getLanguageSync(): Language {
        val sharedPrefs = context.getSharedPreferences(
            "language_preferences",
            Context.MODE_PRIVATE
        )
        val code = sharedPrefs.getString("selected_language", Language.INDONESIAN.code)
        return Language.fromCode(code ?: Language.INDONESIAN.code)
    }
}
