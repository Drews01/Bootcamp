package com.example.bootcamp.data.service

import com.example.bootcamp.domain.service.FCMService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMServiceImpl @Inject constructor() : FCMService {
    override suspend fun getToken(): String? = try {
        FirebaseMessaging.getInstance().token.await()
    } catch (e: Exception) {
        android.util.Log.w("FCMServiceImpl", "Failed to get FCM token", e)
        null
    }
}
