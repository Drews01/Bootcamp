package com.example.bootcamp.domain.repository

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
     * @param fcmToken Optional FCM device token for push notifications
     * @param deviceName Optional device name for identification
     * @param platform Device platform (default ANDROID)
     * @return Result containing success message or failure
     */
    suspend fun login(
        usernameOrEmail: String,
        password: String,
        fcmToken: String? = null,
        deviceName: String? = null,
        platform: String = "ANDROID"
    ): Result<String>

    /**
     * Login with Google ID Token.
     * @param idToken The Google ID Token
     * @param fcmToken Optional FCM device token for push notifications
     * @param deviceName Optional device name for identification
     * @param platform Device platform (default ANDROID)
     * @return Result containing success message or failure
     */
    suspend fun googleLogin(
        idToken: String,
        fcmToken: String? = null,
        deviceName: String? = null,
        platform: String = "ANDROID"
    ): Result<String>

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
    /**
     * Logout the current user.
     * @return Result containing success message or failure
     */
    suspend fun logout(): Result<String>
}
