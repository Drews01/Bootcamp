package com.example.bootcamp.firebase

import android.os.Build
import com.example.bootcamp.data.remote.api.FCMApiService
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.util.ApiResponseHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Firebase Cloud Messaging service for handling push notifications. Handles incoming messages and
 * token refresh events. Automatically registers new tokens with the backend when user is logged in.
 */
@AndroidEntryPoint
class BootcampFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var fcmApiService: FCMApiService
    @Inject lateinit var tokenManager: TokenManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Called when a new FCM token is generated. This occurs when the app is installed, token is
     * deleted, or refreshed. Automatically sends the token to the backend if user is logged in.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        android.util.Log.d(TAG, "New FCM token: $token")
        
        // Send token to backend if user is logged in
        serviceScope.launch {
            sendTokenToServer(token)
        }
    }

    /**
     * Send the FCM token to the backend server.
     * Only sends if user is authenticated (has a valid JWT token).
     */
    private suspend fun sendTokenToServer(fcmToken: String) {
        try {
            // Check if user is logged in
            val jwtToken = tokenManager.token.first()
            if (jwtToken == null) {
                android.util.Log.d(TAG, "User not logged in, skipping FCM token registration")
                return
            }

            val deviceName = Build.MODEL
            val response = fcmApiService.registerToken(fcmToken, deviceName, "ANDROID")
            
            if (response.isSuccessful) {
                android.util.Log.d(TAG, "FCM token registered successfully")
            } else {
                android.util.Log.e(TAG, "Failed to register FCM token: ${response.code()}")
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Exception registering FCM token", e)
        }
    }

    /** Called when a message is received from FCM. */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        android.util.Log.d(TAG, "Message received from: ${remoteMessage.from}")

        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            notificationHelper.showNotification(
                    title = notification.title ?: "Bootcamp",
                    message = notification.body ?: "",
                    data = remoteMessage.data
            )
        }

        // Handle data payload (for custom handling)
        if (remoteMessage.data.isNotEmpty()) {
            android.util.Log.d(TAG, "Data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
    }

    /** Handle data-only messages for custom processing. */
    private fun handleDataMessage(data: Map<String, String>) {
        // Extract data and handle based on type
        val type = data["type"]
        val title = data["title"] ?: "Bootcamp"
        val message = data["message"] ?: data["body"] ?: ""

        when (type) {
            "LOAN_STATUS_CHANGE" -> {
                // Note: Notification is handled automatically by FCM via 'notification' payload from backend.
                // We do nothing here to prevent duplicate notifications.
            }
            "TEST" -> {
                // Handle test notification
                notificationHelper.showNotification(
                    title = title.ifEmpty { "Test Notification" },
                    message = message.ifEmpty { "This is a test notification" },
                    data = data
                )
            }
            "transaction" -> {
                // Handle transaction notification
                notificationHelper.showNotification(
                    title,
                    message,
                    data,
                    NotificationHelper.CHANNEL_TRANSACTIONS
                )
            }
            "promotion" -> {
                // Handle promotional notification
                notificationHelper.showNotification(title, message, data)
            }
            else -> {
                // Default notification
                if (message.isNotEmpty()) {
                    notificationHelper.showNotification(title, message, data)
                }
            }
        }
    }

    companion object {
        private const val TAG = "FCMService"
    }
}
