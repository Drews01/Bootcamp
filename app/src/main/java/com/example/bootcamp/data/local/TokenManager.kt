package com.example.bootcamp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages JWT token and user preferences persistence using DataStore. Injected via Hilt for proper
 * dependency management.
 */
@Singleton
class TokenManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val XSRF_TOKEN_KEY = stringPreferencesKey("xsrf_token")
    }

    /** Flow of the current authentication token. */
    val token: Flow<String?> = dataStore.data.map { preferences -> preferences[TOKEN_KEY] }

    /** Flow of the current username. */
    val username: Flow<String?> = dataStore.data.map { preferences -> preferences[USERNAME_KEY] }

    /** Flow of the current user ID. */
    val userId: Flow<String?> = dataStore.data.map { preferences -> preferences[USER_ID_KEY] }

    /** Flow of the current email. */
    val email: Flow<String?> = dataStore.data.map { preferences -> preferences[EMAIL_KEY] }

    /**
     * Flow of the current XSRF token.
     * IMPORTANT: This stores the **MASKED** CSRF token from the API response body,
     * NOT the raw cookie value. Use this value for the X-XSRF-TOKEN header.
     */
    val xsrfToken: Flow<String?> = dataStore.data.map { preferences -> preferences[XSRF_TOKEN_KEY] }

    /** Save the authentication token. */
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences -> preferences[TOKEN_KEY] = token }
    }

    /** Save the username. */
    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences -> preferences[USERNAME_KEY] = username }
    }

    /** Save the user ID. */
    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences -> preferences[USER_ID_KEY] = userId }
    }

    /** Save the email. */
    suspend fun saveEmail(email: String) {
        dataStore.edit { preferences -> preferences[EMAIL_KEY] = email }
    }

    /**
     * Save the MASKED XSRF token from the API response body.
     * IMPORTANT: This should be the token value from GET /api/csrf-token response,
     * NOT the XSRF-TOKEN cookie value (BREACH protection).
     */
    suspend fun saveXsrfToken(token: String) {
        dataStore.edit { preferences -> preferences[XSRF_TOKEN_KEY] = token }
    }

    /** Save all user data at once. */
    suspend fun saveUserData(token: String, username: String, userId: String? = null, email: String? = null) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USERNAME_KEY] = username
            userId?.let { preferences[USER_ID_KEY] = it }
            email?.let { preferences[EMAIL_KEY] = it }
        }
    }

    /** Clear all stored authentication data. */
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(XSRF_TOKEN_KEY)
        }
    }
}
