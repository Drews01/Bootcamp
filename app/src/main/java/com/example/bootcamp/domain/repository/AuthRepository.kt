package com.example.bootcamp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations. Defines the contract that data layer must
 * implement. Following Dependency Inversion Principle (SOLID).
 */
interface AuthRepository {

    /**
     * Register a new user.
     * @param username The username for the new account
     * @param email The email address for the new account
     * @param password The password for the new account
     * @return Result containing success message or failure
     */
    suspend fun register(username: String, email: String, password: String): Result<String>

    /**
     * Login with username/email and password.
     * @param usernameOrEmail The username or email to login with
     * @param password The password to authenticate
     * @return Result containing success message or failure
     */
    suspend fun login(usernameOrEmail: String, password: String): Result<String>

    /**
     * Request password reset for the given email.
     * @param email The email to send reset instructions to
     * @return Result containing success message or failure
     */
    suspend fun forgotPassword(email: String): Result<String>

    /**
     * Logout the current user.
     * @return Result containing success message or failure
     */
    suspend fun logout(): Result<String>

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

    /**
     * Get user profile details.
     * @return Result with UserProfile on success
     */
    suspend fun getUserProfile(): Result<com.example.bootcamp.domain.model.UserProfile>
}
