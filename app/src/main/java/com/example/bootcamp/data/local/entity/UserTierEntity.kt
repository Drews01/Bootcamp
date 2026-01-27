package com.example.bootcamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching user tier information locally.
 * Used for offline display of homescreen tier card.
 */
@Entity(tableName = "user_tier")
data class UserTierEntity(
    @PrimaryKey
    val id: Long = 1, // Single record, always ID 1
    val tierName: String?,
    val tierCode: String?,
    val creditLimit: Double?,
    val currentUsedAmount: Double?,
    val availableCredit: Double?,
    val totalPaidAmount: Double?,
    val upgradeThreshold: Double?,
    val remainingToUpgrade: Double?,
    val status: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)

