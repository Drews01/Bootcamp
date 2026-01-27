package com.example.bootcamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching branch list locally.
 * Enables offline branch selection for loan submissions.
 */
@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
