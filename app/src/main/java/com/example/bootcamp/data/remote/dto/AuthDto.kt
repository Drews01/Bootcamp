package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

// ============== REQUEST DTOs ==============

/** Request body for user registration. */
data class RegisterRequest(
        @SerializedName("username") val username: String,
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String,
)

/** Request body for user login. */
data class LoginRequest(
        @SerializedName("usernameOrEmail") val usernameOrEmail: String,
        @SerializedName("password") val password: String,
)

/** Request body for forgot password. */
data class ForgotPasswordRequest(
        @SerializedName("email") val email: String,
)

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
        @SerializedName("expiresIn") val expiresIn: Long? = null
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
