package com.example.bootcamp.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.bootcamp.data.local.LanguagePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages application language/locale settings.
 * Provides functionality to change and persist language preferences.
 */
@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val languagePreferences: LanguagePreferences
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * Get the current language as a Flow for reactive updates.
     * Use this in Compose to observe language changes.
     */
    val currentLanguage: Flow<Language> = languagePreferences.selectedLanguage

    /**
     * Get the current language synchronously.
     * Use this for app startup before Compose is ready.
     */
    fun getCurrentLanguageSync(): Language {
        return languagePreferences.getLanguageSync()
    }

    /**
     * Change the app language.
     * This will:
     * 1. Save the preference to DataStore
     * 2. Update AppCompatDelegate locale (for API 33+)
     * 3. Update legacy configuration (for older APIs)
     * 4. Recreate the activity to apply changes
     *
     * @param language The new language to set
     * @param activity The current activity (needed for recreation)
     */
    fun setLanguage(language: Language, activity: Activity? = null) {
        // Write to SharedPreferences SYNCHRONOUSLY first, before any async operations
        // This ensures attachBaseContext will read the correct value after recreation
        val sharedPrefs = context.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("selected_language", language.code).commit()
        
        // Apply locale using AppCompatDelegate (this may trigger automatic recreation)
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
        
        // Update DataStore asynchronously (for ViewModel observation)
        scope.launch {
            languagePreferences.setLanguage(language)
        }
        
        // Fallback: Explicit recreation for immediate effect
        activity?.recreate()
    }

    /**
     * Apply saved language on app startup.
     * Call this in Application.onCreate() or MainActivity.onCreate()
     * BEFORE setContent() is called.
     */
    fun applyStoredLanguage() {
        val language = getCurrentLanguageSync()
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
        updateLocale(context, language)
    }

    /**
     * Update locale for the given context.
     * This is needed for APIs below 33.
     */
    private fun updateLocale(context: Context, language: Language) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * Get a localized context with the specified language.
     * Useful for getting localized strings outside of Compose.
     */
    fun getLocalizedContext(baseContext: Context): Context {
        val language = getCurrentLanguageSync()
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)

        return baseContext.createConfigurationContext(config)
    }
}
