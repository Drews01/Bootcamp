package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserTierLimitDTO(
    @SerializedName("tierName")
    val tierName: String? = null,

    @SerializedName("tierCode")
    val tierCode: String? = null,

    @SerializedName("creditLimit")
    val creditLimit: Double? = null,

    @SerializedName("currentUsedAmount")
    val currentUsedAmount: Double? = null,

    @SerializedName("availableCredit")
    val availableCredit: Double? = null,

    @SerializedName("totalPaidAmount")
    val totalPaidAmount: Double? = null,

    @SerializedName("upgradeThreshold")
    val upgradeThreshold: Double? = null,

    @SerializedName("remainingToUpgrade")
    val remainingToUpgrade: Double? = null,

    @SerializedName("status")
    val status: String? = null
)
