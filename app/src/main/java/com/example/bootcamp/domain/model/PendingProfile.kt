package com.example.bootcamp.domain.model

import com.example.bootcamp.data.local.entity.SyncStatus

/**
 * Domain model representing a pending profile update.
 * Used by UI layer to display and manage offline-queued profile updates.
 */
data class PendingProfile(
    val id: Long,
    val address: String,
    val nik: String,
    val ktpPath: String,
    val phoneNumber: String,
    val accountNumber: String,
    val bankName: String,
    val syncStatus: SyncStatus,
    val errorMessage: String?,
    val retryCount: Int,
    val createdAt: Long
)
