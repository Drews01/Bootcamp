package com.example.bootcamp.domain.model

import com.example.bootcamp.data.local.entity.SyncStatus

/**
 * Domain model representing a pending loan submission.
 * Used by UI layer to display and manage offline-queued loans.
 */
data class PendingLoan(
    val id: Long,
    val amount: Long,
    val tenureMonths: Int,
    val branchId: Long,
    val branchName: String,
    val syncStatus: SyncStatus,
    val errorMessage: String?,
    val retryCount: Int,
    val createdAt: Long
)
