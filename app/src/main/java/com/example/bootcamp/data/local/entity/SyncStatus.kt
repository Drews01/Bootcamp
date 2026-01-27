package com.example.bootcamp.data.local.entity

/**
 * Enum representing the synchronization status of pending operations.
 * Used by PendingLoanEntity and PendingProfileEntity.
 */
enum class SyncStatus {
    /** Operation is waiting to be synced. */
    PENDING,

    /** Operation is currently being synced. */
    SYNCING,

    /** Operation was successfully synced to server. */
    SYNCED,

    /** Operation failed to sync. Check errorMessage for details. */
    FAILED
}
