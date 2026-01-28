package com.example.bootcamp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bootcamp.data.local.entity.PendingProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for PendingProfileEntity.
 * Provides methods for managing pending profile updates in the local database.
 */
@Dao
interface PendingProfileDao {

    /** Insert or replace a pending profile update. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: PendingProfileEntity): Long

    /** Update an existing pending profile. */
    @Update
    suspend fun update(profile: PendingProfileEntity)

    /** Get the most recent pending profile (only one should exist at a time). */
    @Query("SELECT * FROM pending_profiles WHERE syncStatus != 'SYNCED' ORDER BY createdAt DESC LIMIT 1")
    suspend fun getPendingProfile(): PendingProfileEntity?

    /** Get pending profile that needs to be synced. */
    @Query(
        "SELECT * FROM pending_profiles WHERE syncStatus IN ('PENDING', 'FAILED') AND retryCount < 3 ORDER BY createdAt DESC LIMIT 1"
    )
    suspend fun getPendingForSync(): PendingProfileEntity?

    /** Observe the current pending profile as Flow. */
    @Query("SELECT * FROM pending_profiles WHERE syncStatus != 'SYNCED' ORDER BY createdAt DESC LIMIT 1")
    fun observePendingProfile(): Flow<PendingProfileEntity?>

    /** Delete all pending profiles. */
    @Query("DELETE FROM pending_profiles")
    suspend fun clear()

    /** Delete synced profiles. */
    @Query("DELETE FROM pending_profiles WHERE syncStatus = 'SYNCED'")
    suspend fun deleteSynced()

    /** Delete a pending profile by ID. */
    @Query("DELETE FROM pending_profiles WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Check if there's a pending profile. */
    @Query("SELECT COUNT(*) > 0 FROM pending_profiles WHERE syncStatus != 'SYNCED'")
    fun hasPendingProfile(): Flow<Boolean>
}
