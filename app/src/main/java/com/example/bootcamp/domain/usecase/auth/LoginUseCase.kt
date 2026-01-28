package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.data.repository.ProductRepository
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
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
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val loanRepository: LoanRepository,
    private val productRepository: ProductRepository
) :
    UseCaseWithParams<LoginParams, Result<String>> {

    override suspend fun invoke(params: LoginParams): Result<String> {
        // Enforce cache clearing to prevent data leakage from previous sessions
        try {
            userProfileRepository.clearCache()
            loanRepository.clearCache()
            productRepository.clearCache()
        } catch (e: Exception) {
            android.util.Log.w("LoginUseCase", "Failed to clear cache before login", e)
        }

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
