package com.example.bootcamp.data.datasource

import com.example.bootcamp.data.local.dao.PendingProfileDao
import com.example.bootcamp.data.local.dao.UserProfileCacheDao
import com.example.bootcamp.data.local.entity.PendingProfileEntity
import com.example.bootcamp.data.local.entity.UserProfileCacheEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileLocalDataSourceImpl @Inject constructor(
    private val userProfileCacheDao: UserProfileCacheDao,
    private val pendingProfileDao: PendingProfileDao
) : UserProfileLocalDataSource {

    // User Profile Cache
    override fun observeUserProfile(): Flow<UserProfileCacheEntity?> = userProfileCacheDao.observeProfile()

    override suspend fun getUserProfile(): UserProfileCacheEntity? = userProfileCacheDao.getProfile()

    override suspend fun saveUserProfile(profile: UserProfileCacheEntity) {
        userProfileCacheDao.insertOrUpdate(profile)
    }

    override suspend fun clearUserProfile() {
        userProfileCacheDao.clear()
    }

    // Pending Profile Updates
    override fun observePendingProfileUpdate(): Flow<PendingProfileEntity?> = pendingProfileDao.observePendingProfile()

    override suspend fun getPendingProfileUpdate(): PendingProfileEntity? = pendingProfileDao.getPendingProfile()

    override suspend fun savePendingProfileUpdate(update: PendingProfileEntity) {
        pendingProfileDao.insert(update)
    }

    override suspend fun clearPendingProfileUpdate() {
        pendingProfileDao.clear()
    }
}
