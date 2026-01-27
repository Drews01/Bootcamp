package com.example.bootcamp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bootcamp.data.local.entity.UserTierEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user tier caching operations.
 * Stores single user tier record for offline display.
 */
@Dao
interface UserTierDao {

    /** Insert or update user tier (single record). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tier: UserTierEntity)

    /** Get cached user tier. */
    @Query("SELECT * FROM user_tier WHERE id = 1")
    suspend fun getUserTier(): UserTierEntity?

    /** Observe cached user tier as Flow. */
    @Query("SELECT * FROM user_tier WHERE id = 1")
    fun observeUserTier(): Flow<UserTierEntity?>

    /** Clear cached tier. */
    @Query("DELETE FROM user_tier")
    suspend fun clear()
}
