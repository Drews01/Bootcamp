package com.example.bootcamp.data.remote.api

import com.example.bootcamp.data.remote.base.ApiResponse
import com.example.bootcamp.data.remote.dto.BranchDropdownItem
import com.example.bootcamp.data.remote.dto.LoanApplicationDto
import com.example.bootcamp.data.remote.dto.SubmitLoanData
import com.example.bootcamp.data.remote.dto.SubmitLoanRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit service interface for loan-related API endpoints.
 * All endpoints return ApiResponse<T> wrapper.
 */
interface LoanService {

    /**
     * Get list of branches for dropdown selection.
     * @return ApiResponse with list of BranchDropdownItem
     */
    @GET("api/branches/dropdown")
    suspend fun getBranchesDropdown(@retrofit2.http.Header("Authorization") token: String): Response<ApiResponse<List<BranchDropdownItem>>>

    /**
     * Submit a new loan application.
     * @param request Loan submission details (amount, tenureMonths, branchId)
     * @return ApiResponse with SubmitLoanData on success
     */
    @POST("api/loan-workflow/submit")
    suspend fun submitLoan(@Body request: SubmitLoanRequest): Response<ApiResponse<SubmitLoanData>>

    /**
     * Get loan application history for the current user.
     * @return ApiResponse with list of LoanApplicationDto
     */
    @GET("api/loan-applications/my-history")
    suspend fun getLoanHistory(@retrofit2.http.Header("Authorization") token: String): Response<ApiResponse<List<LoanApplicationDto>>>
}
