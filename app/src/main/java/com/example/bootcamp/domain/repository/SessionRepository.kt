package com.example.bootcamp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for session management.
 * Provides real-time updates on user session state.
 * Follows Interface Segregation Principle (ISP) by separating reactive flows from command operations.
 */
interface SessionRepository {

    /**
     * Get the authentication token as a Flow.
     * @return Flow emitting the current token or null
     */
    fun getTokenFlow(): Flow<String?>

    /**
     * Get the current username as a Flow.
     * @return Flow emitting the current username or null
     */
    fun getUsernameFlow(): Flow<String?>

    /**
     * Get the current user ID as a Flow.
     * @return Flow emitting the current user ID or null
     */
    fun getUserIdFlow(): Flow<String?>

    /**
     * Get the current email as a Flow.
     * @return Flow emitting the current email or null
     */
    fun getEmailFlow(): Flow<String?>
}
