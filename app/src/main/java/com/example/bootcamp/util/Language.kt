package com.example.bootcamp.util

/**
 * Enum representing supported languages in the application.
 * Indonesian is the default language.
 */
enum class Language(val code: String, val displayName: String) {
    INDONESIAN("id", "Bahasa Indonesia"),
    ENGLISH("en", "English");

    companion object {
        /**
         * Get Language from language code.
         * Defaults to INDONESIAN if code not found.
         */
        fun fromCode(code: String): Language = entries.find { it.code == code } ?: INDONESIAN
    }
}
