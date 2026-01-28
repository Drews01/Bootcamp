package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.data.repository.ProductRepository
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.domain.usecase.base.UseCaseWithParams
import javax.inject.Inject

/** Parameters for Google Login operation. */
data class GoogleLoginParams(
    val idToken: String,
    val fcmToken: String? = null,
    val deviceName: String? = null,
    val platform: String = "ANDROID"
)

/** Use case for Google login. Encapsulates business logic. */
class GoogleLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val loanRepository: LoanRepository,
    private val productRepository: ProductRepository
) :
    UseCaseWithParams<GoogleLoginParams, Result<String>> {

    override suspend fun invoke(params: GoogleLoginParams): Result<String> {
        // Enforce cache clearing to prevent data leakage from previous sessions
        try {
            userProfileRepository.clearCache()
            loanRepository.clearCache()
            productRepository.clearCache()
        } catch (e: Exception) {
            android.util.Log.w("GoogleLoginUseCase", "Failed to clear cache before login", e)
        }

        if (params.idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("ID Token cannot be empty"))
        }

        return authRepository.googleLogin(
            params.idToken,
            params.fcmToken,
            params.deviceName,
            params.platform
        )
    }
}
