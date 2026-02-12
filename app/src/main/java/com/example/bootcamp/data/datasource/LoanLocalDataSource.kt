package com.example.bootcamp.data.datasource

import com.example.bootcamp.data.local.entity.BranchEntity
import com.example.bootcamp.data.local.entity.LoanHistoryEntity
import com.example.bootcamp.data.local.entity.PendingLoanEntity
import kotlinx.coroutines.flow.Flow

interface LoanLocalDataSource {
    // Branches
    fun observeBranches(): Flow<List<BranchEntity>>
    suspend fun getBranches(): List<BranchEntity>
    suspend fun insertBranches(branches: List<BranchEntity>)
    suspend fun clearBranches()

    // Loan History
    fun observeLoanHistory(): Flow<List<LoanHistoryEntity>>
    suspend fun getLoanHistory(): List<LoanHistoryEntity>
    suspend fun insertLoanHistory(history: List<LoanHistoryEntity>)
    suspend fun clearLoanHistory()
    suspend fun updateLoanStatus(loanId: Long, status: String)

    // Pending Loans
    fun observePendingLoans(): Flow<List<PendingLoanEntity>>
    suspend fun insertPendingLoan(loan: PendingLoanEntity): Long
    suspend fun updatePendingLoan(loan: PendingLoanEntity)
    suspend fun deletePendingLoan(id: Long)
    suspend fun getPendingLoanById(id: Long): PendingLoanEntity?
    suspend fun clearPendingLoans()
}
