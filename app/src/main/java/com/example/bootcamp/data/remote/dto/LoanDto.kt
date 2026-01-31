package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs for loan-related API operations.
 */

// ============== Branch Dropdown ==============

/**
 * Branch item from dropdown API.
 * GET /api/branches/dropdown
 */
data class BranchDropdownItem(@SerializedName("id") val id: Long, @SerializedName("name") val name: String)

// ============== Submit Loan ==============

/**
 * Request body for loan submission.
 * POST /api/loan-workflow/submit
 */
data class SubmitLoanRequest(
    @SerializedName("amount") val amount: Long,
    @SerializedName("tenureMonths") val tenureMonths: Int,
    @SerializedName("branchId") val branchId: Long,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

/**
 * Response data from loan submission.
 */
data class SubmitLoanData(
    @SerializedName("id") val id: Long?,
    @SerializedName("referenceNumber") val referenceNumber: String?,
    @SerializedName("status") val status: String?
)
