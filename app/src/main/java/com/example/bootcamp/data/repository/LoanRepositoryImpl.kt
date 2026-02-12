package com.example.bootcamp.data.repository

import com.example.bootcamp.data.datasource.AuthLocalDataSource
import com.example.bootcamp.data.datasource.LoanLocalDataSource
import com.example.bootcamp.data.datasource.LoanRemoteDataSource
import com.example.bootcamp.data.local.entity.BranchEntity
import com.example.bootcamp.data.local.entity.LoanHistoryEntity
import com.example.bootcamp.data.local.entity.PendingLoanEntity
import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.sync.SyncManager
import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.model.LoanMilestone
import com.example.bootcamp.domain.model.MilestoneStatus
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
    private val authLocalDataSource: AuthLocalDataSource,
    private val loanLocalDataSource: LoanLocalDataSource,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) : BaseRepository(),
    LoanRepository {

    override suspend fun getBranches(): Result<List<Branch>> {
        val token = authLocalDataSource.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        // If online, fetch from remote and cache
        if (networkMonitor.isConnected) {
            val remoteResult = loanRemoteDataSource.getBranches(token)
            val result = mapApiResult(remoteResult) { dto ->
                // Cache branches locally
                val branchEntities = dto.map { BranchEntity(id = it.id, name = it.name) }
                loanLocalDataSource.insertBranches(branchEntities)

                // Return mapped branches
                dto.map { Branch(id = it.id, name = it.name) }
            }

            if (result.isSuccess) return result
        }

        // Offline - return from cache
        val cached = loanLocalDataSource.getBranches()
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
        val token = authLocalDataSource.token.firstOrNull()
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
        loanLocalDataSource.insertPendingLoan(pendingLoan)
        syncManager.scheduleLoanSync()

        return Result.success("Loan queued for submission. Will sync when online.")
    }

    override suspend fun getLoanHistory(): Result<List<LoanApplication>> {
        val token = authLocalDataSource.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        // If online, fetch from remote and cache
        if (networkMonitor.isConnected) {
            val remoteResult = loanRemoteDataSource.getLoanHistory(token)
            val result = mapApiResult(remoteResult) { dto ->
                // Cache loan history
                val historyEntities = dto.map {
                    LoanHistoryEntity(
                        loanApplicationId = it.loanApplicationId,
                        userId = it.userId,
                        productId = it.productId,
                        productName = it.productName,
                        amount = it.amount,
                        tenureMonths = it.tenureMonths,
                        interestRateApplied = it.interestRateApplied,
                        totalAmountToPay = it.totalAmountToPay,
                        currentStatus = it.currentStatus,
                        displayStatus = it.displayStatus,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
                loanLocalDataSource.insertLoanHistory(historyEntities)

                // Return mapped loans
                dto.map {
                    LoanApplication(
                        id = it.loanApplicationId,
                        productId = it.productId,
                        productName = it.productName,
                        amount = it.amount,
                        tenureMonths = it.tenureMonths,
                        status = it.currentStatus,
                        displayStatus = it.displayStatus,
                        date = it.createdAt
                    )
                }
            }

            if (result.isSuccess) return result
        }

        // Offline - return from cache
        return getCachedLoanHistory()
            ?: Result.failure(IllegalStateException("No cached loan history. Please connect to the internet."))
    }

    private suspend fun getCachedLoanHistory(): Result<List<LoanApplication>>? {
        val cached = loanLocalDataSource.getLoanHistory()
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

    override fun observeLoanHistory(): Flow<List<LoanApplication>> =
        loanLocalDataSource.observeLoanHistory().map { entities ->
            entities.map { entity ->
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
        }

    override suspend fun refreshLoanHistory(): Result<Unit> {
        val token = authLocalDataSource.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        if (!networkMonitor.isConnected) {
            return Result.failure(IllegalStateException("No network connection"))
        }

        return mapApiResult(loanRemoteDataSource.getLoanHistory(token)) { dto ->
            val historyEntities = dto.map {
                LoanHistoryEntity(
                    loanApplicationId = it.loanApplicationId,
                    userId = it.userId,
                    productId = it.productId,
                    productName = it.productName,
                    amount = it.amount,
                    tenureMonths = it.tenureMonths,
                    interestRateApplied = it.interestRateApplied,
                    totalAmountToPay = it.totalAmountToPay,
                    currentStatus = it.currentStatus,
                    displayStatus = it.displayStatus,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
            loanLocalDataSource.insertLoanHistory(historyEntities)
        }
    }

    override suspend fun getUserAvailableCredit(): Result<Double> {
        val token = authLocalDataSource.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        return loanRemoteDataSource.getUserTier(token)
            .map { tierDto -> tierDto.availableCredit }
            .asResult()
    }

    override fun observePendingLoans(): Flow<List<PendingLoan>> =
        loanLocalDataSource.observePendingLoans().map { entities ->
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
            val loan = loanLocalDataSource.getPendingLoanById(id)
                ?: return Result.failure(IllegalArgumentException("Loan not found"))

            loanLocalDataSource.updatePendingLoan(
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
        loanLocalDataSource.deletePendingLoan(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getLoanMilestones(loanApplicationId: Long): Result<List<LoanMilestone>> {
        val token = authLocalDataSource.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        return mapApiResult(loanRemoteDataSource.getLoanMilestones(token, loanApplicationId)) { dto ->
            dto.map {
                LoanMilestone(
                    name = it.name,
                    status = MilestoneStatus.valueOf(it.status),
                    timestamp = it.timestamp,
                    order = it.order
                )
            }.sortedBy { it.order }
        }
    }

    /** Clear all cached loan data (e.g., on logout). */
    override suspend fun clearCache() {
        loanLocalDataSource.clearLoanHistory()
        loanLocalDataSource.clearBranches()
        loanLocalDataSource.clearPendingLoans()
    }
}
