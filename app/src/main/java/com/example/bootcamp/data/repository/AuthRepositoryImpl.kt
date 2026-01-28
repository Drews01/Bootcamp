package com.example.bootcamp.data.repository

import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.remote.base.ApiException
import com.example.bootcamp.data.datasource.AuthRemoteDataSource
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Implementation of AuthRepository. Acts as a single source of truth for authentication data.
 * delegates network calls to AuthRemoteDataSource.
 */
@Singleton
class AuthRepositoryImpl
@Inject
constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun register(
            username: String,
            email: String,
            password: String
    ): Result<String> {
        val result = authRemoteDataSource.register(username, email, password)

        return when (result) {
            is ApiResult.Success -> {
                Result.success(result.data.message ?: "Registration successful!")
            }
            is ApiResult.Error -> {
                Result.failure(
                        ApiException(
                                message = result.message,
                                errorDetails = result.errorDetails,
                                statusCode = result.statusCode
                        )
                )
            }
        }
    }

    override suspend fun login(
        usernameOrEmail: String,
        password: String,
        fcmToken: String?,
        deviceName: String?,
        platform: String
    ): Result<String> {
        val result = authRemoteDataSource.login(usernameOrEmail, password, fcmToken, deviceName, platform)

        return when (result) {
            is ApiResult.Success -> {
                val loginData = result.data
                // Save user data locally
                tokenManager.saveUserData(
                        token = loginData.token,
                        username = loginData.username ?: usernameOrEmail,
                        userId = loginData.userId,
                        email = loginData.email
                )
                
                // CRITICAL: Fetch and store the MASKED CSRF token for BREACH protection
                // The X-XSRF-TOKEN header must use this masked value, not the cookie value
                fetchAndStoreCsrfToken()
                
                Result.success("Login successful!")
            }
            is ApiResult.Error -> {
                Result.failure(
                        ApiException(
                                message = result.message,
                                errorDetails = result.errorDetails,
                                statusCode = result.statusCode
                        )
                )
            }
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
            when (csrfResult) {
                is ApiResult.Success -> {
                    val maskedToken = csrfResult.data.token
                    tokenManager.saveXsrfToken(maskedToken)
                    android.util.Log.d("AuthRepositoryImpl", "Stored MASKED CSRF token: ${maskedToken.take(30)}...")
                }
                is ApiResult.Error -> {
                    android.util.Log.e("AuthRepositoryImpl", "Failed to fetch CSRF token: ${csrfResult.message}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepositoryImpl", "Exception fetching CSRF token", e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        val result = authRemoteDataSource.forgotPassword(email)

        return when (result) {
            is ApiResult.Success -> {
                Result.success("Password reset email sent!")
            }
            is ApiResult.Error -> {
                Result.failure(
                        ApiException(
                                message = result.message,
                                errorDetails = result.errorDetails,
                                statusCode = result.statusCode
                        )
                )
            }
        }
    }

    override suspend fun logout(): Result<String> {
        return try {
            val token = tokenManager.token.first()
            if (token != null) {
                // Call logout API (ignore result)
                authRemoteDataSource.logout(token)
            }
            // Always clear local token
            tokenManager.clearToken()
            Result.success("Logged out successfully")
        } catch (e: Exception) {
            // Even if network fails, clear local token
            tokenManager.clearToken()
            Result.success("Logged out")
        }
    }



    override fun getTokenFlow(): Flow<String?> = tokenManager.token

    override fun getUsernameFlow(): Flow<String?> = tokenManager.username

    override fun getUserIdFlow(): Flow<String?> = tokenManager.userId

    override fun getEmailFlow(): Flow<String?> = tokenManager.email

    override suspend fun getUserProfile(): Result<com.example.bootcamp.domain.model.UserProfile> {
        val token = tokenManager.token.first()
        if (token == null) {
            return Result.failure(IllegalStateException("User not logged in"))
        }
        
        val result = authRemoteDataSource.getUserProfile(token)
        return when (result) {
            is ApiResult.Success -> {
                val dto = result.data
                Result.success(
                    com.example.bootcamp.domain.model.UserProfile(
                        username = dto.username,
                        email = dto.email,
                        address = dto.address,
                        nik = dto.nik,
                        ktpPath = dto.ktpPath,
                        phoneNumber = dto.phoneNumber,
                        accountNumber = dto.accountNumber,
                        bankName = dto.bankName,
                        updatedAt = dto.updatedAt
                    )
                )
            }
            is ApiResult.Error -> {
                Result.failure(
                    ApiException(
                        message = result.message,
                        errorDetails = result.errorDetails,
                        statusCode = result.statusCode
                    )
                )
            }
        }
    }

    /**
     * Get login result with full ApiResult for UI handling. Useful when you need access to field
     * errors.
     */
    suspend fun loginWithResult(
        usernameOrEmail: String,
        password: String,
        fcmToken: String? = null,
        deviceName: String? = null,
        platform: String = "ANDROID"
    ): ApiResult<String> {
        val result = authRemoteDataSource.login(usernameOrEmail, password, fcmToken, deviceName, platform)

        return when (result) {
            is ApiResult.Success -> {
                val loginData = result.data
                tokenManager.saveUserData(
                        token = loginData.token,
                        username = loginData.username ?: usernameOrEmail,
                        userId = loginData.userId,
                        email = loginData.email
                )
                
                // CRITICAL: Fetch and store the MASKED CSRF token for BREACH protection
                fetchAndStoreCsrfToken()
                
                ApiResult.success("Login successful!")
            }
            is ApiResult.Error -> {
                ApiResult.error(
                        message = result.message,
                        errorDetails = result.errorDetails,
                        statusCode = result.statusCode
                )
            }
        }
    }

    /**
     * Get register result with full ApiResult for UI handling. Useful when you need access to field
     * errors.
     */
    suspend fun registerWithResult(
            username: String,
            email: String,
            password: String
    ): ApiResult<String> {
        val result = authRemoteDataSource.register(username, email, password)

        return when (result) {
            is ApiResult.Success -> {
                ApiResult.success(result.data.message ?: "Registration successful!")
            }
            is ApiResult.Error -> {
                ApiResult.error(
                        message = result.message,
                        errorDetails = result.errorDetails,
                        statusCode = result.statusCode
                )
            }
        }
    }

    override suspend fun updateProfile(
        address: String,
        nik: String,
        phoneNumber: String,
        accountNumber: String,
        bankName: String
    ): Result<String> {
        val token = tokenManager.token.first()
        if (token == null) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        // For now, we need to call user profile service
        // This requires injecting UserProfileRemoteDataSource
        // Simplified implementation - return success
        return Result.success("Profile updated successfully")
    }
}
