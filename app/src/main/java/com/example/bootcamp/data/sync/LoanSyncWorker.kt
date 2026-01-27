package com.example.bootcamp.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.remote.datasource.LoanRemoteDataSource
import com.example.bootcamp.util.ApiResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

/**
 * WorkManager worker for syncing pending loan submissions to the server.
 * Uses HiltWorker for dependency injection.
 * 
 * Distinguishes between:
 * - Retryable errors: Network issues, server errors (5xx)
 * - Permanent errors: Business logic errors (profile incomplete, active loan, credit limit)
 */
@HiltWorker
class LoanSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingLoanDao: PendingLoanDao,
    private val loanRemoteDataSource: LoanRemoteDataSource,
    private val tokenManager: TokenManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        // Business logic errors that should NOT be retried
        private val PERMANENT_ERROR_PATTERNS = listOf(
            "profile is incomplete",
            "active loan",
            "exceeds remaining credit limit",
            "exceeds credit limit",
            "no tier product available",
            "branch not found",
            "branch id is required",
            "not authenticated",
            "unauthorized"
        )
    }

    override suspend fun doWork(): Result {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure()
        }

        val pendingLoans = pendingLoanDao.getPendingForSync()
        
        if (pendingLoans.isEmpty()) {
            return Result.success()
        }

        var hasRetryableFailure = false

        for (loan in pendingLoans) {
            try {
                // Update status to syncing
                pendingLoanDao.update(loan.copy(
                    syncStatus = SyncStatus.SYNCING,
                    lastAttemptAt = System.currentTimeMillis()
                ))

                // Attempt to submit loan
                val apiResult = loanRemoteDataSource.submitLoan(
                    token = token,
                    amount = loan.amount,
                    tenureMonths = loan.tenureMonths,
                    branchId = loan.branchId
                )

                when (apiResult) {
                    is ApiResult.Success -> {
                        // Mark as synced
                        pendingLoanDao.update(loan.copy(
                            syncStatus = SyncStatus.SYNCED,
                            errorMessage = null,
                            lastAttemptAt = System.currentTimeMillis()
                        ))
                    }
                    is ApiResult.Error -> {
                        val isPermanentError = isPermanentError(apiResult.message, apiResult.statusCode)
                        
                        if (isPermanentError) {
                            // Permanent error - mark as FAILED, don't retry
                            // User needs to take action (complete profile, wait for loan, etc.)
                            pendingLoanDao.update(loan.copy(
                                syncStatus = SyncStatus.FAILED,
                                errorMessage = apiResult.message,
                                retryCount = 999, // Prevent further retries
                                lastAttemptAt = System.currentTimeMillis()
                            ))
                        } else {
                            // Retryable error - increment retry count
                            pendingLoanDao.update(loan.copy(
                                syncStatus = SyncStatus.FAILED,
                                errorMessage = apiResult.message,
                                retryCount = loan.retryCount + 1,
                                lastAttemptAt = System.currentTimeMillis()
                            ))
                            hasRetryableFailure = true
                        }
                    }
                }
            } catch (e: Exception) {
                // Network/unexpected errors are retryable
                pendingLoanDao.update(loan.copy(
                    syncStatus = SyncStatus.FAILED,
                    errorMessage = e.message ?: "Network error",
                    retryCount = loan.retryCount + 1,
                    lastAttemptAt = System.currentTimeMillis()
                ))
                hasRetryableFailure = true
            }
        }

        // Clean up synced loans
        pendingLoanDao.deleteSynced()

        return if (hasRetryableFailure) Result.retry() else Result.success()
    }

    /**
     * Check if the error is a permanent business logic error that shouldn't be retried.
     */
    private fun isPermanentError(message: String, statusCode: Int?): Boolean {
        // 4xx errors (except 408 timeout, 429 rate limit) are usually permanent
        val isPermanentStatusCode = statusCode != null && 
            statusCode in 400..499 && 
            statusCode != 408 && 
            statusCode != 429

        // Check error message patterns
        val matchesPermanentPattern = PERMANENT_ERROR_PATTERNS.any { pattern ->
            message.contains(pattern, ignoreCase = true)
        }

        return isPermanentStatusCode || matchesPermanentPattern
    }
}

