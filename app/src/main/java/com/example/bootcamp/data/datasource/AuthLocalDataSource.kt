package com.example.bootcamp.data.datasource

import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    val token: Flow<String?>
    val username: Flow<String?>
    val userId: Flow<String?>
    val email: Flow<String?>

    suspend fun saveUserData(token: String, username: String, userId: String, email: String)
    suspend fun saveXsrfToken(token: String)
    suspend fun clearToken()
}
