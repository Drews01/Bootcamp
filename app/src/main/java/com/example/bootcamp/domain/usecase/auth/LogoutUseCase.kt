package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.data.remote.api.FCMApiService
import com.example.bootcamp.data.repository.ProductRepository
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.domain.service.FCMService
import com.example.bootcamp.domain.usecase.base.UseCase
import javax.inject.Inject

/**
 * Use case for user logout. Handles clearing authentication state, tokens,
 * and unregistering FCM token from the backend.
 */
open class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val loanRepository: LoanRepository,
    private val productRepository: ProductRepository,
    private val fcmApiService: FCMApiService,
    private val fcmService: FCMService
) : UseCase<Result<String>> {

    override suspend fun invoke(): Result<String> {
        // Unregister FCM token from backend before logout
        try {
            val fcmToken = fcmService.getToken()
            if (fcmToken != null) {
                fcmApiService.unregisterToken(fcmToken)
                println("LogoutUseCase: FCM token unregistered successfully")
            }
        } catch (e: Exception) {
            // Log but don't fail logout if token unregistration fails
            println("LogoutUseCase: Failed to unregister FCM token: ${e.message}")
        }

        // Clear local profile cache
        try {
            userProfileRepository.clearCache()
        } catch (e: Exception) {
            println("LogoutUseCase: Failed to clear profile cache: ${e.message}")
        }

        // Clear loan cache
        try {
            loanRepository.clearCache()
        } catch (e: Exception) {
            println("LogoutUseCase: Failed to clear loan cache: ${e.message}")
        }

        // Clear product/tier cache
        try {
            productRepository.clearCache()
        } catch (e: Exception) {
            println("LogoutUseCase: Failed to clear product cache: ${e.message}")
        }

        return authRepository.logout()
    }
}
