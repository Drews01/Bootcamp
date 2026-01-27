package com.example.bootcamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching loan application history locally.
 * Used for offline display of loan history.
 */
@Entity(tableName = "loan_history")
data class LoanHistoryEntity(
    @PrimaryKey
    val loanApplicationId: Long,
    val userId: Long,
    val productId: Long,
    val productName: String,
    val amount: Double,
    val tenureMonths: Int,
    val interestRateApplied: Double,
    val totalAmountToPay: Double,
    val currentStatus: String,
    val displayStatus: String,
    val createdAt: String,
    val updatedAt: String,
    val lastCached: Long = System.currentTimeMillis()
)
