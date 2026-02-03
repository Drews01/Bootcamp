package com.example.bootcamp.data.datasource

import com.example.bootcamp.data.remote.dto.BranchDropdownItem
import com.example.bootcamp.data.remote.dto.LoanApplicationDto
import com.example.bootcamp.data.remote.dto.LoanMilestoneDto
import com.example.bootcamp.data.remote.dto.SubmitLoanData
import com.example.bootcamp.data.remote.dto.UserTierDto
import com.example.bootcamp.util.ApiResult

/**
 * Interface for loan remote data source.
 * Defines contract for all loan-related network operations.
 */
interface LoanRemoteDataSource {
    suspend fun getBranches(token: String): ApiResult<List<BranchDropdownItem>>
    suspend fun submitLoan(
        token: String,
        amount: Long,
        tenureMonths: Int,
        branchId: Long,
        latitude: Double? = null,
        longitude: Double? = null
    ): ApiResult<SubmitLoanData>
    suspend fun getLoanHistory(token: String): ApiResult<List<LoanApplicationDto>>
    suspend fun getUserTier(token: String): ApiResult<UserTierDto>
    suspend fun getLoanMilestones(token: String, loanApplicationId: Long): ApiResult<List<LoanMilestoneDto>>
}
