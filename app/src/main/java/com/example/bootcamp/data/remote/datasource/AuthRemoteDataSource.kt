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
}
