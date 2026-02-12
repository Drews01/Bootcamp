package com.example.bootcamp.data.repository

import com.example.bootcamp.data.datasource.AuthLocalDataSource
import com.example.bootcamp.data.datasource.AuthRemoteDataSource
import com.example.bootcamp.data.remote.base.ApiException
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.SessionRepository
import com.example.bootcamp.util.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository. Acts as a single source of truth for authentication data.
 * delegates network calls to AuthRemoteDataSource.
 */
@Singleton
class AuthRepositoryImpl
@Inject
constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource
) : BaseRepository(),
    AuthRepository,
    SessionRepository {

    override suspend fun register(username: String, email: String, password: String): Result<String> =
        mapApiResult(authRemoteDataSource.register(username, email, password)) {
            it.message ?: "Registration successful!"
        }

    override suspend fun login(
        usernameOrEmail: String,
        password: String,
        fcmToken: String?,
        deviceName: String?,
        platform: String
    ): Result<String> {
        val result = authRemoteDataSource.login(usernameOrEmail, password, fcmToken, deviceName, platform)

        return mapApiResult(result) { loginData ->
            // Save user data locally
            // Note: Server doesn't return userId, so we use username as the identifier
            authLocalDataSource.saveUserData(
                token = loginData.token,
                username = loginData.username ?: usernameOrEmail,
                userId = loginData.userId ?: loginData.username ?: usernameOrEmail,
                email = loginData.email ?: ""
            )

            // CRITICAL: Fetch and store the MASKED CSRF token for BREACH protection
            fetchAndStoreCsrfToken()

            "Login successful!"
        }
    }

    override suspend fun googleLogin(
        idToken: String,
        fcmToken: String?,
        deviceName: String?,
        platform: String
    ): Result<String> {
        val result = authRemoteDataSource.googleLogin(idToken, fcmToken, deviceName, platform)

        return mapApiResult(result) { loginData ->
            // Save user data locally
            // Note: Server doesn't return userId, so we use username as the identifier
            authLocalDataSource.saveUserData(
                token = loginData.token,
                username = loginData.username ?: "User",
                userId = loginData.userId ?: loginData.username ?: "google_user",
                email = loginData.email ?: ""
            )

            // CRITICAL: Fetch and store the MASKED CSRF token for BREACH protection
            fetchAndStoreCsrfToken()

            "Google Login successful!"
        }
    }

    /**
     * Fetch the MASKED CSRF token from the server and store it.
     * IMPORTANT: This is required for BREACH protection - the masked token from response body
     * must be used for X-XSRF-TOKEN header, not the raw cookie value.
     */
    private suspend fun fetchAndStoreCsrfToken() {
        try {
            val csrfResult = authRemoteDataSource.fetchCsrfToken()
            if (csrfResult is ApiResult.Success) {
                val maskedToken = csrfResult.data.token
                authLocalDataSource.saveXsrfToken(maskedToken)
                android.util.Log.d("AuthRepositoryImpl", "Stored MASKED CSRF token: ${maskedToken.take(30)}...")
            } else if (csrfResult is ApiResult.Error) {
                android.util.Log.e("AuthRepositoryImpl", "Failed to fetch CSRF token: ${csrfResult.message}")
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepositoryImpl", "Exception fetching CSRF token", e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> =
        mapApiResult(authRemoteDataSource.forgotPassword(email)) {
            "Password reset email sent!"
        }

    override suspend fun logout(): Result<String> {
        val token = authLocalDataSource.token.first()

        try {
            if (token != null) {
                authRemoteDataSource.logout(token)
            }
        } catch (e: Exception) {
            // Ignore valid logout errors, just clear local state
        } finally {
            authLocalDataSource.clearToken()
        }

        return Result.success("Logged out successfully")
    }

    override fun getTokenFlow(): Flow<String?> = authLocalDataSource.token

    override fun getUsernameFlow(): Flow<String?> = authLocalDataSource.username

    override fun getUserIdFlow(): Flow<String?> = authLocalDataSource.userId

    override fun getEmailFlow(): Flow<String?> = authLocalDataSource.email
}
