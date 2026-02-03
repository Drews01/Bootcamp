package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO representing a single milestone in the loan application process.
 */
data class LoanMilestoneDto(
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("order") val order: Int
)
