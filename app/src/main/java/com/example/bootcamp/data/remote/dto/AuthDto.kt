package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

// ============== REQUEST DTOs ==============

/** Request body for user registration. */
data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)

/** Request payload for Google Login */
data class GoogleLoginRequest(@SerializedName("idToken") val idToken: String)

/** Request body for user login. Includes optional FCM token for push notification registration. */
data class LoginRequest(
    @SerializedName("usernameOrEmail") val usernameOrEmail: String,
    @SerializedName("password") val password: String,
    @SerializedName("fcmToken") val fcmToken: String? = null,
    @SerializedName("deviceName") val deviceName: String? = null,
    @SerializedName("platform") val platform: String = "ANDROID"
)

/** Request body for forgot password. */
data class ForgotPasswordRequest(@SerializedName("email") val email: String,)

/** Request body for reset password. */
data class ResetPasswordRequest(
    @SerializedName("token") val token: String,
    @SerializedName("newPassword") val newPassword: String,
)

// ============== RESPONSE DATA DTOs ==============

/** Login response data payload. This is the 'data' field in ApiResponse<LoginData>. */
data class LoginData(
    @SerializedName("token") val token: String,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null,
    @SerializedName("expiresIn") val expiresIn: Long? = null,
    @SerializedName("tokenType") val tokenType: String? = null,
    @SerializedName("expiresAt") val expiresAt: String? = null,
    @SerializedName("refreshExpiresAt") val refreshExpiresAt: String? = null,
    @SerializedName("roles") val roles: List<String>? = null
)

/** Register response data payload. This is the 'data' field in ApiResponse<RegisterData>. */
data class RegisterData(
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("message") val message: String? = null
)

/** User profile data. */
data class UserData(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

/**
 * CSRF Token response data. Contains the **masked** token from GET /api/csrf-token.
 * IMPORTANT: This masked token is what should be used in the X-XSRF-TOKEN header.
 * The raw token in the XSRF-TOKEN cookie is different (BREACH protection).
 */
data class CsrfTokenData(
    @SerializedName("token") val token: String,
    // Usually "X-XSRF-TOKEN"
    @SerializedName("headerName") val headerName: String? = null,
    // Usually "_csrf"
    @SerializedName("parameterName") val parameterName: String? = null
)
