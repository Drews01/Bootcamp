package com.example.bootcamp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.bootcamp.MainActivity
import com.example.bootcamp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Helper class for creating and displaying notifications. Handles notification channel creation and
 * notification display.
 */
@Singleton
class NotificationHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        createNotificationChannels()
    }

    /** Create notification channels for Android O and above. */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Default channel for general notifications
            val defaultChannel =
                    NotificationChannel(
                                    CHANNEL_DEFAULT,
                                    "General Notifications",
                                    NotificationManager.IMPORTANCE_DEFAULT
                            )
                            .apply {
                                description = "General app notifications"
                                enableLights(true)
                                enableVibration(true)
                            }

            // High priority channel for important notifications
            val highPriorityChannel =
                    NotificationChannel(
                                    CHANNEL_HIGH_PRIORITY,
                                    "Important Notifications",
                                    NotificationManager.IMPORTANCE_HIGH
                            )
                            .apply {
                                description =
                                        "Important notifications that require immediate attention"
                                enableLights(true)
                                enableVibration(true)
                            }

            // Transaction channel for financial notifications
            val transactionChannel =
                    NotificationChannel(
                                    CHANNEL_TRANSACTIONS,
                                    "Transaction Notifications",
                                    NotificationManager.IMPORTANCE_HIGH
                            )
                            .apply {
                                description = "Notifications about financial transactions"
                                enableLights(true)
                                enableVibration(true)
                            }

            notificationManager.createNotificationChannels(
                    listOf(defaultChannel, highPriorityChannel, transactionChannel)
            )
        }
    }

    /**
     * Show a notification with the given title and message.
     *
     * @param title Notification title
     * @param message Notification body
     * @param data Additional data from the push notification
     * @param channelId Channel to use for the notification
     */
    fun showNotification(
            title: String,
            message: String,
            data: Map<String, String> = emptyMap(),
            channelId: String = CHANNEL_DEFAULT
    ) {
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    // Add data extras for deep linking
                    data.forEach { (key, value) -> putExtra(key, value) }
                }

        val pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        val notification =
                NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

        val notificationId = Random.nextInt()
        notificationManager.notify(notificationId, notification)
    }

    /** Cancel all notifications. */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    /** Cancel a specific notification by ID. */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    companion object {
        const val CHANNEL_DEFAULT = "default_channel"
        const val CHANNEL_HIGH_PRIORITY = "high_priority_channel"
        const val CHANNEL_TRANSACTIONS = "transactions_channel"
    }
}
