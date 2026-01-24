package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO representing a user profile from /api/user-profiles/me endpoint.
 */
data class UserProfileDto(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("address") val address: String?,
    @SerializedName("nik") val nik: String?,
    @SerializedName("ktpPath") val ktpPath: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("accountNumber") val accountNumber: String?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)
