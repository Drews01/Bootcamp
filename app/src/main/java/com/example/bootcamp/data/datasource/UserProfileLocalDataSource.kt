package com.example.bootcamp.data.datasource

import com.example.bootcamp.data.local.entity.PendingProfileEntity
import com.example.bootcamp.data.local.entity.UserProfileCacheEntity
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalDataSource {
    // User Profile Cache
    fun observeUserProfile(): Flow<UserProfileCacheEntity?>
    suspend fun getUserProfile(): UserProfileCacheEntity?
    suspend fun saveUserProfile(profile: UserProfileCacheEntity)
    suspend fun clearUserProfile()

    // Pending Profile Updates
    fun observePendingProfileUpdate(): Flow<PendingProfileEntity?>
    suspend fun getPendingProfileUpdate(): PendingProfileEntity?
    suspend fun savePendingProfileUpdate(update: PendingProfileEntity)
    suspend fun clearPendingProfileUpdate()
}
