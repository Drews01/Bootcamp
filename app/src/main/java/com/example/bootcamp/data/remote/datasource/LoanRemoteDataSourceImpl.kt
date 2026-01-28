package com.example.bootcamp.data.remote.datasource

import com.example.bootcamp.data.datasource.LoanRemoteDataSource
import com.example.bootcamp.data.remote.api.LoanService
import com.example.bootcamp.data.remote.dto.BranchDropdownItem
import com.example.bootcamp.data.remote.dto.LoanApplicationDto
import com.example.bootcamp.data.remote.dto.SubmitLoanData
import com.example.bootcamp.data.remote.dto.SubmitLoanRequest
import com.example.bootcamp.data.remote.dto.UserTierDto
import com.example.bootcamp.util.ApiResponseHandler
import com.example.bootcamp.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source implementation for loan operations.
 * Encapsulates all network calls related to loans and branches.
 */
@Singleton
class LoanRemoteDataSourceImpl @Inject constructor(private val loanService: LoanService) : LoanRemoteDataSource {

    override suspend fun getBranches(token: String): ApiResult<List<BranchDropdownItem>> =
        ApiResponseHandler.safeApiCall {
            loanService.getBranchesDropdown("Bearer $token")
        }

    override suspend fun submitLoan(
        token: String,
        amount: Long,
        tenureMonths: Int,
        branchId: Long
    ): ApiResult<SubmitLoanData> = ApiResponseHandler.safeApiCall {
        loanService.submitLoan(
            "Bearer $token",
            SubmitLoanRequest(
                amount = amount,
                tenureMonths = tenureMonths,
                branchId = branchId
            )
        )
    }

    override suspend fun getLoanHistory(token: String): ApiResult<List<LoanApplicationDto>> =
        ApiResponseHandler.safeApiCall {
            loanService.getLoanHistory("Bearer $token")
        }

    override suspend fun getUserTier(token: String): ApiResult<UserTierDto> = ApiResponseHandler.safeApiCall {
        loanService.getUserTier("Bearer $token")
    }
}
