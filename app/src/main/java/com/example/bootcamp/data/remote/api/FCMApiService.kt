package com.example.bootcamp.data.remote.api

import com.example.bootcamp.data.remote.base.ApiResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service interface for FCM (Firebase Cloud Messaging) API endpoints.
 * Handles device token registration and unregistration for push notifications.
 */
interface FCMApiService {

    /**
     * Register FCM token for push notifications.
     * @param fcmToken The FCM device token
     * @param deviceName Optional device name for identification
     * @param platform Device platform (ANDROID, IOS, WEB)
     * @return ApiResponse indicating success or failure
     */
    @POST("api/fcm/register")
    suspend fun registerToken(
        @Query("fcmToken") fcmToken: String,
        @Query("deviceName") deviceName: String? = null,
        @Query("platform") platform: String = "ANDROID"
    ): Response<ApiResponse<Unit>>

    /**
     * Unregister FCM token (typically called on logout).
     * @param fcmToken The FCM device token to remove
     * @return ApiResponse indicating success or failure
     */
    @DELETE("api/fcm/unregister")
    suspend fun unregisterToken(
        @Query("fcmToken") fcmToken: String
    ): Response<ApiResponse<Unit>>

    /**
     * Send a test notification to the current user's registered devices.
     * @param title Optional notification title
     * @param body Optional notification body
     * @return ApiResponse with count of devices notified
     */
    @POST("api/fcm/test")
    suspend fun testNotification(
        @Query("title") title: String? = null,
        @Query("body") body: String? = null
    ): Response<ApiResponse<Int>>
}
