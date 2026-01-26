package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.usecase.base.UseCaseWithParams
import javax.inject.Inject

/** Parameters for login operation. Includes optional FCM token for push notification registration. */
data class LoginParams(
    val usernameOrEmail: String,
    val password: String,
    val fcmToken: String? = null,
    val deviceName: String? = null,
    val platform: String = "ANDROID"
)

/** Use case for user login. Encapsulates login business logic and validation. */
class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) :
        UseCaseWithParams<LoginParams, Result<String>> {

    override suspend fun invoke(params: LoginParams): Result<String> {
        // Validation
        if (params.usernameOrEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("Username or email cannot be empty"))
        }
        if (params.password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        if (params.password.length < 6) {
            return Result.failure(
                    IllegalArgumentException("Password must be at least 6 characters")
            )
        }

        return authRepository.login(
            params.usernameOrEmail,
            params.password,
            params.fcmToken,
            params.deviceName,
            params.platform
        )
    }
}
