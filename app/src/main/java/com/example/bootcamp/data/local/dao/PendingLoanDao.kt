package com.example.bootcamp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bootcamp.data.local.entity.PendingLoanEntity
import com.example.bootcamp.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for PendingLoanEntity.
 * Provides methods for managing pending loan submissions in the local database.
 */
@Dao
interface PendingLoanDao {

    /** Insert a new pending loan. Returns the generated ID. */
    @Insert
    suspend fun insert(loan: PendingLoanEntity): Long

    /** Update an existing pending loan. */
    @Update
    suspend fun update(loan: PendingLoanEntity)

    /** Delete a pending loan. */
    @Delete
    suspend fun delete(loan: PendingLoanEntity)

    /** Delete a pending loan by ID. */
    @Query("DELETE FROM pending_loans WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Get all pending loans by sync status. */
    @Query("SELECT * FROM pending_loans WHERE syncStatus = :status ORDER BY createdAt ASC")
    suspend fun getByStatus(status: SyncStatus): List<PendingLoanEntity>

    /** Get all pending loans that need to be synced (PENDING or FAILED with retry limit not exceeded). */
    @Query("SELECT * FROM pending_loans WHERE syncStatus IN ('PENDING', 'FAILED') AND retryCount < 3 ORDER BY createdAt ASC")
    suspend fun getPendingForSync(): List<PendingLoanEntity>

    /** Get all pending loans as observable Flow. */
    @Query("SELECT * FROM pending_loans ORDER BY createdAt DESC")
    fun getAllPendingLoans(): Flow<List<PendingLoanEntity>>

    /** Get a pending loan by ID. */
    @Query("SELECT * FROM pending_loans WHERE id = :id")
    suspend fun getById(id: Long): PendingLoanEntity?

    /** Delete all synced loans. */
    @Query("DELETE FROM pending_loans WHERE syncStatus = 'SYNCED'")
    suspend fun deleteSynced()

    /** Get count of pending loans not yet synced. */
    @Query("SELECT COUNT(*) FROM pending_loans WHERE syncStatus != 'SYNCED'")
    fun getPendingCount(): Flow<Int>

    /** Clear all pending loans. */
    @Query("DELETE FROM pending_loans")
    suspend fun clearAll()
}
