package com.example.bootcamp.domain.usecase.loan

import com.example.bootcamp.domain.model.LoanMilestone
import com.example.bootcamp.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Use case for fetching loan milestones.
 */
class GetLoanMilestonesUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    suspend operator fun invoke(loanApplicationId: Long): Result<List<LoanMilestone>> {
        return loanRepository.getLoanMilestones(loanApplicationId)
    }
}
