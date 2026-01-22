package com.example.bootcamp.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Firebase Cloud Messaging service for handling push notifications. Handles incoming messages and
 * token refresh events.
 */
@AndroidEntryPoint
class BootcampFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationHelper: NotificationHelper

    /**
     * Called when a new FCM token is generated. This occurs when the app is installed, token is
     * deleted, or refreshed.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send token to your backend server for push notification targeting
        // You can inject a repository here to save the token
        android.util.Log.d(TAG, "New FCM token: $token")
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
        val message = data["message"] ?: ""

        when (type) {
            "transaction" -> {
                // Handle transaction notification
                notificationHelper.showNotification(title, message, data)
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
