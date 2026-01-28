package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.data.remote.api.FCMApiService
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.usecase.base.UseCase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Use case for user logout. Handles clearing authentication state, tokens,
 * and unregistering FCM token from the backend.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val fcmApiService: FCMApiService
) : UseCase<Result<String>> {

    override suspend fun invoke(): Result<String> {
        // Unregister FCM token from backend before logout
        try {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            fcmApiService.unregisterToken(fcmToken)
            android.util.Log.d("LogoutUseCase", "FCM token unregistered successfully")
        } catch (e: Exception) {
            // Log but don't fail logout if token unregistration fails
            android.util.Log.w("LogoutUseCase", "Failed to unregister FCM token", e)
        }

        return authRepository.logout()
    }
}
