package com.example.bootcamp.data.datasource

import com.example.bootcamp.data.local.dao.BranchDao
import com.example.bootcamp.data.local.dao.LoanHistoryDao
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.entity.BranchEntity
import com.example.bootcamp.data.local.entity.LoanHistoryEntity
import com.example.bootcamp.data.local.entity.PendingLoanEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoanLocalDataSourceImpl @Inject constructor(
    private val branchDao: BranchDao,
    private val loanHistoryDao: LoanHistoryDao,
    private val pendingLoanDao: PendingLoanDao
) : LoanLocalDataSource {

    // Branches
    override fun observeBranches(): Flow<List<BranchEntity>> = branchDao.observeBranches()

    override suspend fun getBranches(): List<BranchEntity> = branchDao.getAllBranches()

    override suspend fun insertBranches(branches: List<BranchEntity>) {
        branchDao.insertAll(branches)
    }

    override suspend fun clearBranches() {
        branchDao.clearAll()
    }

    // Loan History
    override fun observeLoanHistory(): Flow<List<LoanHistoryEntity>> = loanHistoryDao.observeHistory()

    override suspend fun getLoanHistory(): List<LoanHistoryEntity> = loanHistoryDao.getAllHistory()

    override suspend fun insertLoanHistory(loans: List<LoanHistoryEntity>) {
        loanHistoryDao.insertAll(loans)
    }

    override suspend fun clearLoanHistory() {
        loanHistoryDao.clearAll()
    }

    override suspend fun updateLoanStatus(loanId: Long, status: String) {
        loanHistoryDao.updateLoanStatus(loanId, status)
    }

    // Pending Loans
    override fun observePendingLoans(): Flow<List<PendingLoanEntity>> = pendingLoanDao.getAllPendingLoans()

    override suspend fun insertPendingLoan(loan: PendingLoanEntity): Long = pendingLoanDao.insert(loan)

    override suspend fun updatePendingLoan(loan: PendingLoanEntity) {
        pendingLoanDao.update(loan)
    }

    override suspend fun deletePendingLoan(id: Long) {
        pendingLoanDao.deleteById(id)
    }

    override suspend fun getPendingLoanById(id: Long): PendingLoanEntity? = pendingLoanDao.getById(id)

    override suspend fun clearPendingLoans() {
        pendingLoanDao.clearAll()
    }
}
