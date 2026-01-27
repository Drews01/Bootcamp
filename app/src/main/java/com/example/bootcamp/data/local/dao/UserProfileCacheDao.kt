package com.example.bootcamp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bootcamp.data.local.entity.UserProfileCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user profile caching operations.
 * Stores single user profile record for offline display.
 */
@Dao
interface UserProfileCacheDao {

    /** Insert or update user profile (single record). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileCacheEntity)

    /** Get cached user profile. */
    @Query("SELECT * FROM user_profile_cache WHERE id = 1")
    suspend fun getProfile(): UserProfileCacheEntity?

    /** Observe cached user profile as Flow. */
    @Query("SELECT * FROM user_profile_cache WHERE id = 1")
    fun observeProfile(): Flow<UserProfileCacheEntity?>

    /** Clear cached profile. */
    @Query("DELETE FROM user_profile_cache")
    suspend fun clear()
}
