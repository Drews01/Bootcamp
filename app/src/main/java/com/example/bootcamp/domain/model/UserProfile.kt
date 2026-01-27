package com.example.bootcamp.domain.model

/**
 * Domain model representing user profile details.
 */
data class UserProfile(
    val username: String,
    val email: String,
    val address: String?,
    val nik: String?,
    val ktpPath: String?,
    val phoneNumber: String?,
    val accountNumber: String?,
    val bankName: String?,
    val updatedAt: String?,
    /** True if this profile is pending sync (queued offline). */
    val isPending: Boolean = false
)
