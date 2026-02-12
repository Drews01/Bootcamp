package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.domain.repository.SessionRepository
import com.example.bootcamp.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing authentication state. Returns a Flow that emits the current authentication
 * token.
 */
class GetAuthStateUseCase @Inject constructor(private val sessionRepository: SessionRepository) : FlowUseCase<String?> {

    override fun invoke(): Flow<String?> = sessionRepository.getTokenFlow()
}
