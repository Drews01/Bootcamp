package com.example.bootcamp.data.remote.datasource

import com.example.bootcamp.data.remote.api.AuthService
import com.example.bootcamp.data.remote.dto.ForgotPasswordRequest
import com.example.bootcamp.data.remote.dto.LoginData
import com.example.bootcamp.data.remote.dto.LoginRequest
import com.example.bootcamp.data.remote.dto.RegisterData
import com.example.bootcamp.data.remote.dto.RegisterRequest
import com.example.bootcamp.util.ApiResponseHandler
import com.example.bootcamp.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for authentication operations. Encapsulates all network calls related to
 * authentication. Acts as a bridge between Repository and API Service.
 */
@Singleton
class AuthRemoteDataSource @Inject constructor(private val authService: AuthService) {

    /**
     * Register a new user.
     * @param username User's username
     * @param email User's email
     * @param password User's password
     * @return ApiResult with RegisterData on success
     */
    suspend fun register(
            username: String,
            email: String,
            password: String
    ): ApiResult<RegisterData> {
        return ApiResponseHandler.safeApiCall {
            authService.register(RegisterRequest(username, email, password))
        }
    }

    /**
     * Login with username/email and password.
     * @param usernameOrEmail Username or email
     * @param password Password
     * @return ApiResult with LoginData on success
     */
    suspend fun login(usernameOrEmail: String, password: String): ApiResult<LoginData> {
        return ApiResponseHandler.safeApiCall {
            authService.login(LoginRequest(usernameOrEmail, password))
        }
    }

    /**
     * Request password reset email.
     * @param email Email address
     * @return ApiResult with Unit on success
     */
    suspend fun forgotPassword(email: String): ApiResult<Unit> {
        return ApiResponseHandler.safeApiCall {
            authService.forgotPassword(ForgotPasswordRequest(email))
        }
    }

    /**
     * Logout current user.
     * @param token Authorization token
     * @return ApiResult with Unit on success
     */
    suspend fun logout(token: String): ApiResult<Unit> {
        return ApiResponseHandler.safeApiCall { authService.logout("Bearer $token") }
    }

    /**
     * Get current user profile.
     * @param token Authorization token
     * @return ApiResult with UserData on success
     */
    suspend fun getCurrentUser(
            token: String
    ): ApiResult<com.example.bootcamp.data.remote.dto.UserData> {
        return ApiResponseHandler.safeApiCall { authService.getCurrentUser("Bearer $token") }
    }

    /**
     * Refresh access token.
     * @param token Current token
     * @return ApiResult with new LoginData on success
     */
    suspend fun refreshToken(token: String): ApiResult<LoginData> {
        return ApiResponseHandler.safeApiCall { authService.refreshToken("Bearer $token") }
    }

    /**
     * Get user profile details.
     * @param token Authorization token
     * @return ApiResult with UserProfileDto on success
     */
    suspend fun getUserProfile(
            token: String
    ): ApiResult<com.example.bootcamp.data.remote.dto.UserProfileDto> {
        return ApiResponseHandler.safeApiCall { authService.getUserProfile("Bearer $token") }
    }

    /**
     * Fetch a fresh CSRF token from the server.
     * IMPORTANT: This returns the **MASKED** token from the response body.
     * The masked token must be stored and used for the X-XSRF-TOKEN header.
     * The raw token in the XSRF-TOKEN cookie is different (BREACH protection).
     * 
     * NOTE: This endpoint returns direct JSON, not wrapped in ApiResponse.
     * 
     * @return ApiResult with CsrfTokenData containing the masked token
     */
    suspend fun fetchCsrfToken(): ApiResult<com.example.bootcamp.data.remote.dto.CsrfTokenData> {
        return try {
            val response = authService.getCsrfToken()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.success(body)
                } else {
                    ApiResult.error(message = "CSRF token response body is null")
                }
            } else {
                ApiResult.error(message = "Failed to fetch CSRF token: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.error(message = "Exception fetching CSRF token: ${e.message}")
        }
    }
}
