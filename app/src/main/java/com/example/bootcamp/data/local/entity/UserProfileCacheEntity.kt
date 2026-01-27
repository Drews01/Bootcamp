package com.example.bootcamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching user profile locally.
 * Used for offline display of profile details.
 */
@Entity(tableName = "user_profile_cache")
data class UserProfileCacheEntity(
    @PrimaryKey
    val id: Long = 1, // Single record, always ID 1
    val username: String,
    val email: String,
    val address: String?,
    val nik: String?,
    val ktpPath: String?,
    val phoneNumber: String?,
    val accountNumber: String?,
    val bankName: String?,
    val updatedAt: String?,
    val lastCached: Long = System.currentTimeMillis()
)
