package com.example.bootcamp.domain.service

/**
 * Interface for interactions with Firebase Cloud Messaging.
 * Abstracts dependencies like token retrieval to allow for easier testing.
 */
interface FCMService {
    /**
     * Retrieves the current FCM registration token.
     * @return The token string, or null if retrieval fails.
     */
    suspend fun getToken(): String?
}
