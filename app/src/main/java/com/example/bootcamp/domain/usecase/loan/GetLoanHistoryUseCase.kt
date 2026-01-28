package com.example.bootcamp.domain.usecase.loan

import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Use case to fetch loan application history for the current user.
 */
class GetLoanHistoryUseCase @Inject constructor(private val loanRepository: LoanRepository) {
    suspend operator fun invoke(): Result<List<LoanApplication>> = loanRepository.getLoanHistory()
}
