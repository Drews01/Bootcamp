package com.example.bootcamp.domain.usecase.loan

import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe loan application history reactively from local cache.
 * Emits new data whenever the underlying Room database changes.
 */
class ObserveLoanHistoryUseCase @Inject constructor(private val loanRepository: LoanRepository) {
    operator fun invoke(): Flow<List<LoanApplication>> = loanRepository.observeLoanHistory()
}
