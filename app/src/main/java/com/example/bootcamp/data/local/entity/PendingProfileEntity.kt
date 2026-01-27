package com.example.bootcamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a pending profile update that will be synced when online.
 * Stores profile data locally until it can be submitted to the server.
 */
@Entity(tableName = "pending_profiles")
data class PendingProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** User's address. */
    val address: String,

    /** User's NIK (National ID Number). */
    val nik: String,

    /** Path to KTP image (must be uploaded before queuing). */
    val ktpPath: String,

    /** User's phone number. */
    val phoneNumber: String,

    /** User's bank account number. */
    val accountNumber: String,

    /** User's bank name. */
    val bankName: String,

    /** Current sync status. */
    val syncStatus: SyncStatus = SyncStatus.PENDING,

    /** Error message if sync failed. */
    val errorMessage: String? = null,

    /** Number of sync retry attempts. */
    val retryCount: Int = 0,

    /** Timestamp when the profile update was queued. */
    val createdAt: Long = System.currentTimeMillis(),

    /** Timestamp of the last sync attempt. */
    val lastAttemptAt: Long? = null
)
