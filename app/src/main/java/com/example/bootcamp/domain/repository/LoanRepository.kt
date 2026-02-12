package com.example.bootcamp.domain.repository

import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.model.LoanMilestone
import com.example.bootcamp.domain.model.PendingLoan
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for loan-related operations. Follows the repository pattern from Clean
 * Architecture.
 */
interface LoanRepository {

    /**
     * Get list of branches for dropdown selection.
     * @return Result with list of Branch on success
     */
    suspend fun getBranches(): Result<List<Branch>>

    /**
     * Submit a loan application.
     * If online, submits directly. If offline, queues for later sync.
     * @param amount Loan amount
     * @param tenureMonths Loan tenure in months
     * @param branchId Selected branch ID
     * @param branchName Selected branch name (for offline display)
     * @return Result with success message on success
     */
    suspend fun submitLoan(
        amount: Long,
        tenureMonths: Int,
        branchId: Long,
        branchName: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): Result<String>

    /**
     * Get loan application history.
     * @return Result with list of LoanApplication
     */
    suspend fun getLoanHistory(): Result<List<com.example.bootcamp.domain.model.LoanApplication>>

    /**
     * Observe loan application history reactively from local cache.
     * Emits whenever the cached data changes.
     * @return Flow of LoanApplication list
     */
    fun observeLoanHistory(): Flow<List<com.example.bootcamp.domain.model.LoanApplication>>

    /**
     * Force-refresh loan history from the remote server and update local cache.
     * @return Result indicating success or failure of the refresh
     */
    suspend fun refreshLoanHistory(): Result<Unit>

    /**
     * Get milestones for a specific loan application.
     * @param loanApplicationId The loan application ID
     * @return Result with list of LoanMilestone
     */
    suspend fun getLoanMilestones(loanApplicationId: Long): Result<List<LoanMilestone>>

    /**
     * Get user's current tier and available credit.
     * @return Result with available credit amount
     */
    suspend fun getUserAvailableCredit(): Result<Double>

    /**
     * Observe all pending loans as Flow.
     * @return Flow of pending loan list
     */
    fun observePendingLoans(): Flow<List<PendingLoan>>

    /**
     * Retry syncing a specific pending loan.
     * @param id Pending loan ID
     * @return Result indicating success or failure
     */
    suspend fun retryPendingLoan(id: Long): Result<Unit>

    /**
     * Delete a pending loan.
     * @param id Pending loan ID
     * @return Result indicating success or failure
     */
    suspend fun deletePendingLoan(id: Long): Result<Unit>

    /** Clear all cached loan data. */
    suspend fun clearCache()
}
