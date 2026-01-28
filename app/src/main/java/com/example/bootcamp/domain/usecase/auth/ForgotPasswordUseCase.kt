package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.usecase.base.UseCaseWithParams
import javax.inject.Inject

/** Parameters for forgot password operation. */
data class ForgotPasswordParams(val email: String)

/**
 * Use case for requesting password reset. Encapsulates forgot password business logic and
 * validation.
 */
class ForgotPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) :
    UseCaseWithParams<ForgotPasswordParams, Result<String>> {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override suspend fun invoke(params: ForgotPasswordParams): Result<String> {
        // Validation
        if (params.email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        if (!emailRegex.matches(params.email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        return authRepository.forgotPassword(params.email)
    }
}
