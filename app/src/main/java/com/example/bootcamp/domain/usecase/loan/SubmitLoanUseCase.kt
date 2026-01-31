package com.example.bootcamp.domain.usecase.loan

import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.usecase.base.UseCaseWithParams
import javax.inject.Inject

/** Parameters for loan submission. */
data class SubmitLoanParams(
    val amount: Long,
    val tenureMonths: Int,
    val branchId: Long,
    val branchName: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

/** UseCase to submit a loan application. Validates input before submission. */
class SubmitLoanUseCase @Inject constructor(private val loanRepository: LoanRepository) :
    UseCaseWithParams<SubmitLoanParams, Result<String>> {

    override suspend fun invoke(params: SubmitLoanParams): Result<String> {
        // Validation
        if (params.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be positive"))
        }
        if (params.tenureMonths <= 0) {
            return Result.failure(IllegalArgumentException("Tenure must be positive"))
        }

        return loanRepository.submitLoan(
            amount = params.amount,
            tenureMonths = params.tenureMonths,
            branchId = params.branchId,
            branchName = params.branchName,
            latitude = params.latitude,
            longitude = params.longitude
        )
    }
}
