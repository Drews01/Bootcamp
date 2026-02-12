package com.example.bootcamp.domain.model

/**
 * Domain model for profile update operations.
 * Replaces UserProfileRequest DTO in the domain layer to prevent DTO leakage.
 */
data class ProfileUpdate(
    val address: String,
    val nik: String,
    val ktpPath: String,
    val phoneNumber: String,
    val accountNumber: String,
    val bankName: String
)
