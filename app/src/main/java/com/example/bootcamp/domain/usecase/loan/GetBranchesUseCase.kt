package com.example.bootcamp.domain.usecase.loan

import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.usecase.base.UseCase
import javax.inject.Inject

/** UseCase to fetch list of branches. */
class GetBranchesUseCase @Inject constructor(private val loanRepository: LoanRepository) :
        UseCase<Result<List<Branch>>> {

    override suspend fun invoke(): Result<List<Branch>> {
        return loanRepository.getBranches()
    }
}
