package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.usecase.base.UseCase
import javax.inject.Inject

/** Use case for user logout. Handles clearing authentication state and tokens. */
class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) :
        UseCase<Result<String>> {

    override suspend fun invoke(): Result<String> {
        return authRepository.logout()
    }
}
