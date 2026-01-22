package com.example.bootcamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room entity representing cached user data. Used for offline-first functionality. */
@Entity(tableName = "users")
data class UserEntity(
        @PrimaryKey val id: String,
        val username: String,
        val email: String,
        val token: String?,
        val lastUpdated: Long = System.currentTimeMillis()
)
