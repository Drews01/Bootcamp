package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO representing a loan application in the history list.
 */
data class LoanApplicationDto(
    @SerializedName("loanApplicationId") val loanApplicationId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("productId") val productId: Long,
    @SerializedName("amount") val amount: Double,
    @SerializedName("tenureMonths") val tenureMonths: Int,
    @SerializedName("interestRateApplied") val interestRateApplied: Double,
    @SerializedName("totalAmountToPay") val totalAmountToPay: Double,
    @SerializedName("currentStatus") val currentStatus: String,
    @SerializedName("displayStatus") val displayStatus: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)
