package com.example.bootcamp.domain.model

/**
 * Domain model representing a loan application history item.
 */
data class LoanApplication(
    val id: Long,
    val productId: Long,
    val productName: String,
    val amount: Double,
    val tenureMonths: Int,
    val status: String,
    val displayStatus: String,
    val date: String
)
