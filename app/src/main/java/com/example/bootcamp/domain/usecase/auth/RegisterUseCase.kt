package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.usecase.base.UseCaseWithParams
import javax.inject.Inject

/** Parameters for registration operation. */
data class RegisterParams(val username: String, val email: String, val password: String, val confirmPassword: String)

/** Use case for user registration. Encapsulates registration business logic and validation. */
class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) :
    UseCaseWithParams<RegisterParams, Result<String>> {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override suspend fun invoke(params: RegisterParams): Result<String> {
        // Validation
        if (params.username.isBlank()) {
            return Result.failure(IllegalArgumentException("Username cannot be empty"))
        }
        if (params.username.length < 3) {
            return Result.failure(
                IllegalArgumentException("Username must be at least 3 characters")
            )
        }
        if (params.email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        if (!emailRegex.matches(params.email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        if (params.password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        if (params.password.length < 6) {
            return Result.failure(
                IllegalArgumentException("Password must be at least 6 characters")
            )
        }
        if (params.password != params.confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match"))
        }

        return authRepository.register(params.username, params.email, params.password)
    }
}
