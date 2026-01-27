package com.example.bootcamp.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.dao.PendingProfileDao
import com.example.bootcamp.util.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central manager for handling offline sync operations.
 * Monitors network connectivity and schedules sync workers when online.
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val pendingLoanDao: PendingLoanDao,
    private val pendingProfileDao: PendingProfileDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val workManager = WorkManager.getInstance(context)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    /** Observable count of pending items waiting to be synced. */
    val pendingCount: Flow<Int> = combine(
        pendingLoanDao.getPendingCount(),
        pendingProfileDao.hasPendingProfile()
    ) { loanCount, hasProfile ->
        loanCount + if (hasProfile) 1 else 0
    }

    init {
        // Observe network changes and trigger sync when online
        scope.launch {
            networkMonitor.connectivityFlow.collect { isConnected ->
                if (isConnected) {
                    scheduleSync()
                }
            }
        }
    }

    /** Schedule all sync workers to run. */
    fun scheduleSync() {
        scheduleLoanSync()
        scheduleProfileSync()
    }

    /** Schedule loan sync worker with network constraint. */
    fun scheduleLoanSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<LoanSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            LOAN_SYNC_WORK,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /** Schedule profile sync worker with network constraint. */
    fun scheduleProfileSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<ProfileSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            PROFILE_SYNC_WORK,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /** Trigger immediate sync if network is available. */
    fun triggerImmediateSync() {
        if (networkMonitor.isConnected) {
            scheduleSync()
        }
    }

    companion object {
        const val LOAN_SYNC_WORK = "loan_sync_work"
        const val PROFILE_SYNC_WORK = "profile_sync_work"
    }
}
