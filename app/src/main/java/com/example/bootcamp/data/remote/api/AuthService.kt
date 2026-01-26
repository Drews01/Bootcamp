package com.example.bootcamp.data.remote.api

import com.example.bootcamp.data.remote.base.ApiResponse
import com.example.bootcamp.data.remote.dto.CsrfTokenData
import com.example.bootcamp.data.remote.dto.ForgotPasswordRequest
import com.example.bootcamp.data.remote.dto.LoginData
import com.example.bootcamp.data.remote.dto.LoginRequest
import com.example.bootcamp.data.remote.dto.RegisterData
import com.example.bootcamp.data.remote.dto.RegisterRequest
import com.example.bootcamp.data.remote.dto.ResetPasswordRequest
import com.example.bootcamp.data.remote.dto.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit service interface for authentication API endpoints. All endpoints return ApiResponse<T>
 * wrapper.
 */
interface AuthService {

        /**
         * Register a new user.
         * @param request Registration details
         * @return ApiResponse with RegisterData on success
         */
        @POST("auth/register")
        suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<RegisterData>>

        /**
         * Login with username/email and password.
         * @param request Login credentials
         * @return ApiResponse with LoginData containing token
         */
        @POST("auth/login")
        suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginData>>

        /**
         * Request password reset email.
         * @param request Email address
         * @return ApiResponse with no data (success/error only)
         */
        @POST("auth/forgot-password")
        suspend fun forgotPassword(
                @Body request: ForgotPasswordRequest
        ): Response<ApiResponse<Unit>>

        /**
         * Reset password with token.
         * @param request Reset token and new password
         * @return ApiResponse with no data (success/error only)
         */
        @POST("auth/reset-password")
        suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

        /**
         * Logout current user.
         * @param token Authorization token
         * @return ApiResponse with no data (success/error only)
         */
        @POST("auth/logout")
        suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Unit>>

        /**
         * Get current user profile.
         * @param token Authorization token
         * @return ApiResponse with UserData
         */
        @GET("auth/me")
        suspend fun getCurrentUser(
                @Header("Authorization") token: String
        ): Response<ApiResponse<UserData>>

        /**
         * Refresh access token.
         * @param token Current token
         * @return ApiResponse with new LoginData
         */
        @POST("auth/refresh")
        suspend fun refreshToken(
                @Header("Authorization") token: String
        ): Response<ApiResponse<LoginData>>

        /**
         * Get a fresh CSRF token. Returns the **masked** token in the response body.
         * IMPORTANT: The X-XSRF-TOKEN header must use this masked token, NOT the cookie value.
         * The cookie contains the raw token, the response body contains the masked token (BREACH protection).
         * 
         * NOTE: This endpoint returns direct JSON { "token": "...", "headerName": "..." },
         * NOT wrapped in ApiResponse.
         * 
         * @return CsrfTokenData containing the masked token
         */
        @GET("api/csrf-token") 
        suspend fun getCsrfToken(): Response<CsrfTokenData>

        /**
         * Get user profile details.
         * @param token Authorization token
         * @return ApiResponse with UserProfileDto
         */
        @GET("api/user-profiles/me")
        suspend fun getUserProfile(
                @Header("Authorization") token: String
        ): Response<ApiResponse<com.example.bootcamp.data.remote.dto.UserProfileDto>>
}
