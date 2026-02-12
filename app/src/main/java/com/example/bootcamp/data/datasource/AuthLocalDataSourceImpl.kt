package com.example.bootcamp.data.datasource

import com.example.bootcamp.data.local.TokenManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthLocalDataSourceImpl @Inject constructor(private val tokenManager: TokenManager) : AuthLocalDataSource {
    override val token: Flow<String?> = tokenManager.token
    override val username: Flow<String?> = tokenManager.username
    override val userId: Flow<String?> = tokenManager.userId
    override val email: Flow<String?> = tokenManager.email

    override suspend fun saveUserData(token: String, username: String, userId: String, email: String) {
        tokenManager.saveUserData(token, username, userId, email)
    }

    override suspend fun saveXsrfToken(token: String) {
        tokenManager.saveXsrfToken(token)
    }

    override suspend fun clearToken() {
        tokenManager.clearToken()
    }
}
