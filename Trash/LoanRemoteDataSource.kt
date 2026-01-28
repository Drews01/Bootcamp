package com.example.bootcamp.data.remote.datasource

import com.example.bootcamp.data.remote.api.LoanService
import com.example.bootcamp.data.remote.dto.BranchDropdownItem
import com.example.bootcamp.data.remote.dto.LoanApplicationDto
import com.example.bootcamp.data.remote.dto.SubmitLoanData
import com.example.bootcamp.data.remote.dto.SubmitLoanRequest
import com.example.bootcamp.util.ApiResponseHandler
import com.example.bootcamp.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for loan operations.
 * Encapsulates all network calls related to loans and branches.
 */
@Singleton
class LoanRemoteDataSource @Inject constructor(
    private val loanService: LoanService
) {

    /**
     * Get list of branches for dropdown.
     * @return ApiResult with list of BranchDropdownItem
     */
    suspend fun getBranches(token: String): ApiResult<List<BranchDropdownItem>> {
        return ApiResponseHandler.safeApiCall {
            loanService.getBranchesDropdown("Bearer $token")
        }
    }

    /**
     * Submit a loan application.
     * @param amount Loan amount in currency units
     * @param tenureMonths Loan tenure in months
     * @param branchId Selected branch ID
     * @return ApiResult with SubmitLoanData on success
     */
    suspend fun submitLoan(
        token: String,
        amount: Long,
        tenureMonths: Int,
        branchId: Long
    ): ApiResult<SubmitLoanData> {
        return ApiResponseHandler.safeApiCall {
            loanService.submitLoan(
                "Bearer $token",
                SubmitLoanRequest(
                    amount = amount,
                    tenureMonths = tenureMonths,
                    branchId = branchId
                )
            )
        }
    }

    /**
     * Get loan application history.
     * @param token Authentication token
     * @return ApiResult with list of LoanApplicationDto
     */
    suspend fun getLoanHistory(token: String): ApiResult<List<LoanApplicationDto>> {
        return ApiResponseHandler.safeApiCall {
            loanService.getLoanHistory("Bearer $token")
        }
    }

    /**
     * Get user's current tier and credit information.
     * @param token Authentication token
     * @return ApiResult with UserTierDto
     */
    suspend fun getUserTier(token: String): ApiResult<com.example.bootcamp.data.remote.dto.UserTierDto> {
        return ApiResponseHandler.safeApiCall {
            loanService.getUserTier("Bearer $token")
        }
    }
}
