package com.example.bootcamp.data.repository

import android.net.Uri
import com.example.bootcamp.data.datasource.AuthLocalDataSource
import com.example.bootcamp.data.datasource.UserProfileLocalDataSource
import com.example.bootcamp.data.datasource.UserProfileRemoteDataSource
import com.example.bootcamp.data.local.entity.PendingProfileEntity
import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.local.entity.UserProfileCacheEntity
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.data.sync.SyncManager
import com.example.bootcamp.domain.model.PendingProfile
import com.example.bootcamp.domain.model.ProfileUpdate
import com.example.bootcamp.domain.model.UserProfile
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Implementation of UserProfileRepository with offline-first logic. */
@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserProfileRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) : BaseRepository(),
    UserProfileRepository {

    override suspend fun submitProfile(update: ProfileUpdate): Result<UserProfile> {
        return try {
            val token = authLocalDataSource.token.first()
                ?: return Result.failure(Exception("Not authenticated"))

            // Map domain model to DTO for remote call
            val request = UserProfileRequest(
                address = update.address,
                nik = update.nik,
                ktpPath = update.ktpPath,
                phoneNumber = update.phoneNumber,
                accountNumber = update.accountNumber,
                bankName = update.bankName
            )

            // Try remote first if online
            if (networkMonitor.isConnected) {
                val remoteResult = remoteDataSource.submitProfile(token, request)
                val result = mapApiResult(remoteResult) { dto ->
                    val profile = UserProfile(
                        username = dto.username,
                        email = dto.email,
                        address = dto.address,
                        nik = dto.nik,
                        ktpPath = dto.ktpPath,
                        phoneNumber = dto.phoneNumber,
                        accountNumber = dto.accountNumber,
                        bankName = dto.bankName,
                        updatedAt = dto.updatedAt
                    )
                    // Cache the profile
                    cacheProfile(profile)
                    profile
                }

                if (result.isSuccess) return result
            }

            // Queue for offline sync
            val pendingProfile = PendingProfileEntity(
                address = update.address,
                nik = update.nik,
                ktpPath = update.ktpPath,
                phoneNumber = update.phoneNumber,
                accountNumber = update.accountNumber,
                bankName = update.bankName,
                syncStatus = SyncStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )
            userProfileLocalDataSource.savePendingProfileUpdate(pendingProfile)
            syncManager.scheduleProfileSync()

            // Return a "queued" profile for UI display
            Result.success(
                UserProfile(
                    username = "",
                    email = "",
                    address = update.address,
                    nik = update.nik,
                    ktpPath = update.ktpPath,
                    phoneNumber = update.phoneNumber,
                    accountNumber = update.accountNumber,
                    bankName = update.bankName,
                    updatedAt = null,
                    isPending = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadKtp(imageUri: Uri): Result<String> {
        return try {
            val token = authLocalDataSource.token.first()
                ?: return Result.failure(Exception("Not authenticated"))

            return mapApiResult(remoteDataSource.uploadKtp(token, imageUri)) {
                it.ktpPath ?: ""
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val token = authLocalDataSource.token.first()
                ?: return Result.failure(Exception("Not authenticated"))

            // If online, fetch from remote and cache
            if (networkMonitor.isConnected) {
                val remoteResult = remoteDataSource.getUserProfile(token)
                val result = mapApiResult(remoteResult) { dto ->
                    val profile = UserProfile(
                        username = dto.username ?: "",
                        email = dto.email ?: "",
                        address = dto.address,
                        nik = dto.nik,
                        ktpPath = dto.ktpPath,
                        phoneNumber = dto.phoneNumber,
                        accountNumber = dto.accountNumber,
                        bankName = dto.bankName,
                        updatedAt = dto.updatedAt
                    )
                    // Cache the profile
                    cacheProfile(profile)
                    profile
                }

                if (result.isSuccess) return result
            }

            // Offline - return from cache
            getCachedProfile()
                ?: Result.failure(Exception("No cached profile. Please connect to the internet."))
        } catch (e: Exception) {
            // Network error, try cache
            getCachedProfile() ?: Result.failure(e)
        }
    }

    private suspend fun cacheProfile(profile: UserProfile) {
        val entity = UserProfileCacheEntity(
            username = profile.username,
            email = profile.email,
            address = profile.address,
            nik = profile.nik,
            ktpPath = profile.ktpPath,
            phoneNumber = profile.phoneNumber,
            accountNumber = profile.accountNumber,
            bankName = profile.bankName,
            updatedAt = profile.updatedAt
        )
        userProfileLocalDataSource.saveUserProfile(entity)
    }

    private suspend fun getCachedProfile(): Result<UserProfile>? {
        val cached = userProfileLocalDataSource.getUserProfile() ?: return null
        val profile = UserProfile(
            username = cached.username,
            email = cached.email,
            address = cached.address,
            nik = cached.nik,
            ktpPath = cached.ktpPath,
            phoneNumber = cached.phoneNumber,
            accountNumber = cached.accountNumber,
            bankName = cached.bankName,
            updatedAt = cached.updatedAt
        )
        return Result.success(profile)
    }

    override fun observePendingProfile(): Flow<PendingProfile?> =
        userProfileLocalDataSource.observePendingProfileUpdate().map { entity ->
            entity?.let {
                PendingProfile(
                    id = it.id,
                    address = it.address,
                    nik = it.nik,
                    ktpPath = it.ktpPath,
                    phoneNumber = it.phoneNumber,
                    accountNumber = it.accountNumber,
                    bankName = it.bankName,
                    syncStatus = it.syncStatus,
                    errorMessage = it.errorMessage,
                    retryCount = it.retryCount,
                    createdAt = it.createdAt
                )
            }
        }

    override suspend fun retryPendingProfile(): Result<Unit> {
        return try {
            val profile = userProfileLocalDataSource.getPendingProfileUpdate()
                ?: return Result.failure(IllegalArgumentException("No pending profile found"))

            userProfileLocalDataSource.savePendingProfileUpdate(
                profile.copy(
                    syncStatus = SyncStatus.PENDING,
                    retryCount = 0,
                    errorMessage = null
                )
            )
            syncManager.scheduleProfileSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearPendingProfile(): Result<Unit> = try {
        userProfileLocalDataSource.clearPendingProfileUpdate()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Clear cached profile data (e.g., on logout). */
    override suspend fun clearCache() {
        userProfileLocalDataSource.clearUserProfile()
        userProfileLocalDataSource.clearPendingProfileUpdate()
    }
}
