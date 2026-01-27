package com.example.bootcamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a pending loan submission that will be synced when online.
 * Stores loan data locally until it can be submitted to the server.
 */
@Entity(tableName = "pending_loans")
data class PendingLoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Loan amount requested. */
    val amount: Long,

    /** Loan tenure in months. */
    val tenureMonths: Int,

    /** Selected branch ID. */
    val branchId: Long,

    /** Cached branch name for offline display. */
    val branchName: String,

    /** Current sync status. */
    val syncStatus: SyncStatus = SyncStatus.PENDING,

    /** Error message if sync failed. */
    val errorMessage: String? = null,

    /** Number of sync retry attempts. */
    val retryCount: Int = 0,

    /** Timestamp when the loan was queued. */
    val createdAt: Long = System.currentTimeMillis(),

    /** Timestamp of the last sync attempt. */
    val lastAttemptAt: Long? = null
)
