package com.example.bootcamp.data.datasource

import com.example.bootcamp.data.remote.dto.CsrfTokenData
import com.example.bootcamp.data.remote.dto.LoginData
import com.example.bootcamp.data.remote.dto.RegisterData
import com.example.bootcamp.data.remote.dto.UserData
import com.example.bootcamp.data.remote.dto.UserProfileDto
import com.example.bootcamp.util.ApiResult

/**
 * Interface for authentication remote data source.
 * Defines contract for all authentication-related network operations.
 */
interface AuthRemoteDataSource {
    suspend fun register(username: String, email: String, password: String): ApiResult<RegisterData>
    suspend fun login(
        usernameOrEmail: String,
        password: String,
        fcmToken: String? = null,
        deviceName: String? = null,
        platform: String = "ANDROID"
    ): ApiResult<LoginData>
    suspend fun googleLogin(
        idToken: String,
        fcmToken: String? = null,
        deviceName: String? = null,
        platform: String = "ANDROID"
    ): ApiResult<LoginData>
    suspend fun forgotPassword(email: String): ApiResult<Unit>
    suspend fun logout(token: String): ApiResult<Unit>
    suspend fun getCurrentUser(token: String): ApiResult<UserData>
    suspend fun refreshToken(token: String): ApiResult<LoginData>
    suspend fun getUserProfile(token: String): ApiResult<UserProfileDto>
    suspend fun fetchCsrfToken(): ApiResult<CsrfTokenData>
}
