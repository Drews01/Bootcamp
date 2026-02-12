package com.example.bootcamp.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bootcamp.data.datasource.UserProfileRemoteDataSource
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.local.dao.PendingProfileDao
import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.util.ApiResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

/**
 * WorkManager worker for syncing pending profile updates to the server.
 * Uses HiltWorker for dependency injection.
 *
 * Distinguishes between:
 * - Retryable errors: Network issues, server errors (5xx)
 * - Permanent errors: Validation errors, auth errors
 */
@HiltWorker
class ProfileSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingProfileDao: PendingProfileDao,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val tokenManager: TokenManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        // Profile errors that should NOT be retried
        private val PERMANENT_ERROR_PATTERNS = listOf(
            "invalid",
            "validation",
            "not authenticated",
            "unauthorized",
            "already exists",
            "duplicate"
        )
    }

    override suspend fun doWork(): Result {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure()
        }

        val pendingProfile = pendingProfileDao.getPendingForSync()
            ?: return Result.success() // Nothing to sync

        try {
            // Update status to syncing
            pendingProfileDao.update(
                pendingProfile.copy(
                    syncStatus = SyncStatus.SYNCING,
                    lastAttemptAt = System.currentTimeMillis()
                )
            )

            // Create request from pending profile
            val request = UserProfileRequest(
                address = pendingProfile.address,
                nik = pendingProfile.nik,
                ktpPath = pendingProfile.ktpPath,
                phoneNumber = pendingProfile.phoneNumber,
                accountNumber = pendingProfile.accountNumber,
                bankName = pendingProfile.bankName
            )

            // Attempt to submit profile
            val result = userProfileRemoteDataSource.submitProfile(token, request)

            return when (result) {
                is ApiResult.Success -> {
                    // Mark as synced
                    pendingProfileDao.update(
                        pendingProfile.copy(
                            syncStatus = SyncStatus.SYNCED,
                            errorMessage = null,
                            lastAttemptAt = System.currentTimeMillis()
                        )
                    )
                    // Clean up synced profiles
                    pendingProfileDao.deleteSynced()
                    Result.success()
                }
                is ApiResult.Error -> {
                    val errorMessage = result.message
                    val statusCode = result.statusCode
                    val isPermanent = isPermanentError(errorMessage, statusCode)

                    if (isPermanent) {
                        // Permanent error - don't retry
                        pendingProfileDao.update(
                            pendingProfile.copy(
                                syncStatus = SyncStatus.FAILED,
                                errorMessage = errorMessage,
                                retryCount = 999, // Prevent further retries
                                lastAttemptAt = System.currentTimeMillis()
                            )
                        )
                        Result.success() // Don't retry permanent errors
                    } else {
                        // Retryable error
                        pendingProfileDao.update(
                            pendingProfile.copy(
                                syncStatus = SyncStatus.FAILED,
                                errorMessage = errorMessage,
                                retryCount = pendingProfile.retryCount + 1,
                                lastAttemptAt = System.currentTimeMillis()
                            )
                        )
                        Result.retry()
                    }
                }
            }
        } catch (e: Exception) {
            // Network errors are retryable
            pendingProfileDao.update(
                pendingProfile.copy(
                    syncStatus = SyncStatus.FAILED,
                    errorMessage = e.message ?: "Network error",
                    retryCount = pendingProfile.retryCount + 1,
                    lastAttemptAt = System.currentTimeMillis()
                )
            )
            return Result.retry()
        }
    }

    /**
     * Check if the error is a permanent error that shouldn't be retried.
     */
    private fun isPermanentError(message: String, statusCode: Int?): Boolean {
        // 4xx errors (except 408 timeout, 429 rate limit) are usually permanent
        val isPermanentStatusCode = statusCode != null && statusCode in 400..499 &&
            statusCode != 408 &&
            statusCode != 429

        // Check error message patterns
        val matchesPermanentPattern = PERMANENT_ERROR_PATTERNS.any { pattern ->
            message.contains(pattern, ignoreCase = true)
        }

        return isPermanentStatusCode || matchesPermanentPattern
    }
}
