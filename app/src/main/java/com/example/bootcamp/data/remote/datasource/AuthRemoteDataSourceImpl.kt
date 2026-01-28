package com.example.bootcamp.data.remote.datasource

import com.example.bootcamp.data.datasource.AuthRemoteDataSource
import com.example.bootcamp.data.remote.api.AuthService
import com.example.bootcamp.data.remote.dto.CsrfTokenData
import com.example.bootcamp.data.remote.dto.ForgotPasswordRequest
import com.example.bootcamp.data.remote.dto.LoginData
import com.example.bootcamp.data.remote.dto.LoginRequest
import com.example.bootcamp.data.remote.dto.RegisterData
import com.example.bootcamp.data.remote.dto.RegisterRequest
import com.example.bootcamp.data.remote.dto.UserData
import com.example.bootcamp.data.remote.dto.UserProfileDto
import com.example.bootcamp.util.ApiResponseHandler
import com.example.bootcamp.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source implementation for authentication operations. Encapsulates all network calls related to
 * authentication. Acts as a bridge between Repository and API Service.
 */
@Singleton
class AuthRemoteDataSourceImpl @Inject constructor(private val authService: AuthService) : AuthRemoteDataSource {

    override suspend fun register(username: String, email: String, password: String): ApiResult<RegisterData> =
        ApiResponseHandler.safeApiCall {
            authService.register(RegisterRequest(username, email, password))
        }

    override suspend fun login(
        usernameOrEmail: String,
        password: String,
        fcmToken: String?,
        deviceName: String?,
        platform: String
    ): ApiResult<LoginData> = ApiResponseHandler.safeApiCall {
        authService.login(LoginRequest(usernameOrEmail, password, fcmToken, deviceName, platform))
    }

    override suspend fun googleLogin(
        idToken: String,
        fcmToken: String?,
        deviceName: String?,
        platform: String
    ): ApiResult<LoginData> = ApiResponseHandler.safeApiCall {
        authService.googleLogin(com.example.bootcamp.data.remote.dto.GoogleLoginRequest(idToken))
    }

    override suspend fun forgotPassword(email: String): ApiResult<Unit> = ApiResponseHandler.safeApiCall {
        authService.forgotPassword(ForgotPasswordRequest(email))
    }

    override suspend fun logout(token: String): ApiResult<Unit> = ApiResponseHandler.safeApiCall {
        authService.logout("Bearer $token")
    }

    override suspend fun getCurrentUser(token: String): ApiResult<UserData> =
        ApiResponseHandler.safeApiCall { authService.getCurrentUser("Bearer $token") }

    override suspend fun refreshToken(token: String): ApiResult<LoginData> = ApiResponseHandler.safeApiCall {
        authService.refreshToken("Bearer $token")
    }

    override suspend fun getUserProfile(token: String): ApiResult<UserProfileDto> =
        ApiResponseHandler.safeApiCall { authService.getUserProfile("Bearer $token") }

    override suspend fun fetchCsrfToken(): ApiResult<CsrfTokenData> = try {
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
