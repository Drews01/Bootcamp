package com.example.bootcamp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bootcamp.data.local.entity.BranchEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for branch caching operations.
 * Provides methods to cache and retrieve branch list for offline use.
 */
@Dao
interface BranchDao {

    /** Insert or update multiple branches. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(branches: List<BranchEntity>)

    /** Get all cached branches. */
    @Query("SELECT * FROM branches ORDER BY name ASC")
    suspend fun getAllBranches(): List<BranchEntity>

    /** Observe all cached branches as Flow. */
    @Query("SELECT * FROM branches ORDER BY name ASC")
    fun observeBranches(): Flow<List<BranchEntity>>

    /** Get branch by ID. */
    @Query("SELECT * FROM branches WHERE id = :id")
    suspend fun getBranchById(id: Long): BranchEntity?

    /** Clear all cached branches. */
    @Query("DELETE FROM branches")
    suspend fun clearAll()

    /** Get count of cached branches. */
    @Query("SELECT COUNT(*) FROM branches")
    suspend fun getCount(): Int
}
