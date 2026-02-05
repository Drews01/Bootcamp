package com.example.bootcamp.domain.model

/**
 * Domain model representing a loan milestone.
 */
data class LoanMilestone(val name: String, val status: MilestoneStatus, val timestamp: String?, val order: Int)

/**
 * Status of a loan milestone.
 */
enum class MilestoneStatus {
    COMPLETED,
    CURRENT,
    PENDING
}
