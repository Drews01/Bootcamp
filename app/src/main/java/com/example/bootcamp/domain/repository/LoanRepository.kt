package com.example.bootcamp.domain.repository

import com.example.bootcamp.domain.model.Branch

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
     * @param amount Loan amount
     * @param tenureMonths Loan tenure in months
     * @param branchId Selected branch ID
     * @return Result with success message on success
     */
    suspend fun submitLoan(amount: Long, tenureMonths: Int, branchId: Long): Result<String>

    /**
     * Get loan application history.
     * @return Result with list of LoanApplication
     */
    suspend fun getLoanHistory(): Result<List<com.example.bootcamp.domain.model.LoanApplication>>
}
