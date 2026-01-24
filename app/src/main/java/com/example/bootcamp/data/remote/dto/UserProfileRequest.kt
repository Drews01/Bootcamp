package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Request DTO for creating/updating user profile. */
data class UserProfileRequest(
        @SerializedName("address") val address: String,
        @SerializedName("nik") val nik: String,
        @SerializedName("ktpPath") val ktpPath: String,
        @SerializedName("phoneNumber") val phoneNumber: String,
        @SerializedName("accountNumber") val accountNumber: String,
        @SerializedName("bankName") val bankName: String
)
