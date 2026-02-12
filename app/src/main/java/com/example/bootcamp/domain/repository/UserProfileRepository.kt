package com.example.bootcamp.domain.repository

import android.net.Uri
import com.example.bootcamp.domain.model.PendingProfile
import com.example.bootcamp.domain.model.ProfileUpdate
import com.example.bootcamp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/** Repository interface for user profile operations. */
interface UserProfileRepository {

    /**
     * Submit/update user profile.
     * If online, submits directly. If offline, queues for later sync.
     * @param update Profile data to submit (domain model)
     * @return Result containing UserProfile on success or failure
     */
    suspend fun submitProfile(update: ProfileUpdate): Result<UserProfile>

    /**
     * Upload KTP image.
     * @param imageUri URI of the image to upload
     * @return Result containing the KTP path on success or failure
     */
    suspend fun uploadKtp(imageUri: Uri): Result<String>

    /**
     * Get user profile.
     * @return Result containing UserProfile on success or failure
     */
    suspend fun getUserProfile(): Result<UserProfile>

    /**
     * Observe pending profile update as Flow.
     * @return Flow of pending profile (null if none)
     */
    fun observePendingProfile(): Flow<PendingProfile?>

    /**
     * Retry syncing the pending profile.
     * @return Result indicating success or failure
     */
    suspend fun retryPendingProfile(): Result<Unit>

    /**
     * Clear the pending profile.
     * @return Result indicating success or failure
     */
    suspend fun clearPendingProfile(): Result<Unit>

    /** Clear cached profile data. */
    suspend fun clearCache()
}
