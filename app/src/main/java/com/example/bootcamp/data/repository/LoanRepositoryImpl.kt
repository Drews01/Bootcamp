package com.example.bootcamp.data.repository

import com.example.bootcamp.data.datasource.LoanRemoteDataSource
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.local.dao.BranchDao
import com.example.bootcamp.data.local.dao.LoanHistoryDao
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.entity.BranchEntity
import com.example.bootcamp.data.local.entity.LoanHistoryEntity
import com.example.bootcamp.data.local.entity.PendingLoanEntity
import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.sync.SyncManager
import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.model.PendingLoan
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.util.ApiResult
import com.example.bootcamp.util.NetworkMonitor
import com.example.bootcamp.util.asResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Implementation of LoanRepository with offline-first logic. */
@Singleton
class LoanRepositoryImpl @Inject constructor(
    private val loanRemoteDataSource: LoanRemoteDataSource,
    private val tokenManager: TokenManager,
    private val pendingLoanDao: PendingLoanDao,
    private val branchDao: BranchDao,
    private val loanHistoryDao: LoanHistoryDao,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) : LoanRepository {

    override suspend fun getBranches(): Result<List<Branch>> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        // If online, fetch from remote and cache
        if (networkMonitor.isConnected) {
            val remoteResult = loanRemoteDataSource.getBranches(token)

            when (remoteResult) {
                is ApiResult.Success -> {
                    // Cache branches locally
                    val branchEntities = remoteResult.data.map { dto ->
                        BranchEntity(id = dto.id, name = dto.name)
                    }
                    branchDao.insertAll(branchEntities)

                    // Return mapped branches
                    val branches = remoteResult.data.map { dto ->
                        Branch(id = dto.id, name = dto.name)
                    }
                    return Result.success(branches)
                }
                is ApiResult.Error -> {
                    // If remote fails, try to return cached data
                    val cached = branchDao.getAllBranches()
                    return if (cached.isNotEmpty()) {
                        Result.success(cached.map { Branch(id = it.id, name = it.name) })
                    } else {
                        remoteResult.asResult()
                    }
                }
            }
        }

        // Offline - return from cache
        val cached = branchDao.getAllBranches()
        return if (cached.isNotEmpty()) {
            Result.success(cached.map { Branch(id = it.id, name = it.name) })
        } else {
            Result.failure(IllegalStateException("No cached branches available. Please connect to the internet."))
        }
    }

    override suspend fun submitLoan(
        amount: Long,
        tenureMonths: Int,
        branchId: Long,
        branchName: String,
        latitude: Double?,
        longitude: Double?
    ): Result<String> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        // Try remote first if online
        if (networkMonitor.isConnected) {
            val apiResult = loanRemoteDataSource
                .submitLoan(token, amount, tenureMonths, branchId, latitude, longitude)

            return when (apiResult) {
                is ApiResult.Success -> {
                    val data = apiResult.data
                    Result.success("Loan submitted successfully. Reference: ${data.referenceNumber ?: data.id}")
                }
                is ApiResult.Error -> {
                    // Check if this is a business logic error (4xx) that should NOT be retried
                    val statusCode = apiResult.statusCode
                    val isBusinessError =
                        statusCode != null && statusCode in 400..499 && statusCode != 408 && statusCode != 429

                    if (isBusinessError) {
                        // Return the actual error - don't queue for offline sync
                        apiResult.asResult()
                    } else {
                        // Network/server error (5xx, timeout, rate limit) - fall through to queue
                        queueLoanForOfflineSync(amount, tenureMonths, branchId, branchName)
                    }
                }
            }
        }

        // Offline - queue for sync
        return queueLoanForOfflineSync(amount, tenureMonths, branchId, branchName)
    }

    private suspend fun queueLoanForOfflineSync(
        amount: Long,
        tenureMonths: Int,
        branchId: Long,
        branchName: String
    ): Result<String> {
        val pendingLoan = PendingLoanEntity(
            amount = amount,
            tenureMonths = tenureMonths,
            branchId = branchId,
            branchName = branchName,
            syncStatus = SyncStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
        pendingLoanDao.insert(pendingLoan)
        syncManager.scheduleLoanSync()

        return Result.success("Loan queued for submission. Will sync when online.")
    }

    override suspend fun getLoanHistory(): Result<List<LoanApplication>> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        // If online, fetch from remote and cache
        if (networkMonitor.isConnected) {
            val remoteResult = loanRemoteDataSource.getLoanHistory(token)

            when (remoteResult) {
                is ApiResult.Success -> {
                    // Cache loan history
                    val historyEntities = remoteResult.data.map { dto ->
                        LoanHistoryEntity(
                            loanApplicationId = dto.loanApplicationId,
                            userId = dto.userId,
                            productId = dto.productId,
                            productName = dto.productName,
                            amount = dto.amount,
                            tenureMonths = dto.tenureMonths,
                            interestRateApplied = dto.interestRateApplied,
                            totalAmountToPay = dto.totalAmountToPay,
                            currentStatus = dto.currentStatus,
                            displayStatus = dto.displayStatus,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    }
                    loanHistoryDao.insertAll(historyEntities)

                    // Return mapped loans
                    val loans = remoteResult.data.map { dto ->
                        LoanApplication(
                            id = dto.loanApplicationId,
                            productId = dto.productId,
                            productName = dto.productName,
                            amount = dto.amount,
                            tenureMonths = dto.tenureMonths,
                            status = dto.currentStatus,
                            displayStatus = dto.displayStatus,
                            date = dto.createdAt
                        )
                    }
                    return Result.success(loans)
                }
                is ApiResult.Error -> {
                    // If remote fails, try to return cached data
                    return getCachedLoanHistory()
                        ?: remoteResult.asResult()
                }
            }
        }

        // Offline - return from cache
        return getCachedLoanHistory()
            ?: Result.failure(IllegalStateException("No cached loan history. Please connect to the internet."))
    }

    private suspend fun getCachedLoanHistory(): Result<List<LoanApplication>>? {
        val cached = loanHistoryDao.getAllHistory()
        if (cached.isEmpty()) return null

        val loans = cached.map { entity ->
            LoanApplication(
                id = entity.loanApplicationId,
                productId = entity.productId,
                productName = entity.productName,
                amount = entity.amount,
                tenureMonths = entity.tenureMonths,
                status = entity.currentStatus,
                displayStatus = entity.displayStatus,
                date = entity.createdAt
            )
        }
        return Result.success(loans)
    }

    override suspend fun getUserAvailableCredit(): Result<Double> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        return loanRemoteDataSource.getUserTier(token)
            .map { tierDto -> tierDto.availableCredit }
            .asResult()
    }

    override fun getPendingLoans(): Flow<List<PendingLoan>> = pendingLoanDao.getAllPendingLoans().map { entities ->
        entities.map { entity ->
            PendingLoan(
                id = entity.id,
                amount = entity.amount,
                tenureMonths = entity.tenureMonths,
                branchId = entity.branchId,
                branchName = entity.branchName,
                syncStatus = entity.syncStatus,
                errorMessage = entity.errorMessage,
                retryCount = entity.retryCount,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun retryPendingLoan(id: Long): Result<Unit> {
        return try {
            val loan = pendingLoanDao.getById(id)
                ?: return Result.failure(IllegalArgumentException("Loan not found"))

            pendingLoanDao.update(
                loan.copy(
                    syncStatus = SyncStatus.PENDING,
                    retryCount = 0,
                    errorMessage = null
                )
            )
            syncManager.scheduleLoanSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePendingLoan(id: Long): Result<Unit> = try {
        pendingLoanDao.deleteById(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Clear all cached loan data (e.g., on logout). */
    override suspend fun clearCache() {
        loanHistoryDao.clearAll()
        branchDao.clearAll()
        pendingLoanDao.clearAll()
    }
}
