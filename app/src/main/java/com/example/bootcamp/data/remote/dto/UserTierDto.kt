package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO representing user tier information from /api/user-products/my-tier endpoint.
 */
data class UserTierDto(
    @SerializedName("tierName") val tierName: String,
    @SerializedName("tierCode") val tierCode: String,
    @SerializedName("tierOrder") val tierOrder: Int,
    @SerializedName("creditLimit") val creditLimit: Double,
    @SerializedName("currentUsedAmount") val currentUsedAmount: Double,
    @SerializedName("availableCredit") val availableCredit: Double,
    @SerializedName("totalPaidAmount") val totalPaidAmount: Double,
    @SerializedName("upgradeThreshold") val upgradeThreshold: Double,
    @SerializedName("remainingToUpgrade") val remainingToUpgrade: Double,
    @SerializedName("interestRate") val interestRate: Double,
    @SerializedName("status") val status: String
)
