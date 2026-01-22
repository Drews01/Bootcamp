package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.usecase.base.FlowUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing authentication state. Returns a Flow that emits the current authentication
 * token.
 */
class GetAuthStateUseCase @Inject constructor(private val authRepository: AuthRepository) :
        FlowUseCase<String?> {

    override fun invoke(): Flow<String?> {
        return authRepository.getTokenFlow()
    }
}
