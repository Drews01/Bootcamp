package com.example.bootcamp.data.repository

import android.net.Uri
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.local.dao.PendingProfileDao
import com.example.bootcamp.data.local.dao.UserProfileCacheDao
import com.example.bootcamp.data.local.entity.PendingProfileEntity
import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.local.entity.UserProfileCacheEntity
import com.example.bootcamp.data.remote.datasource.UserProfileRemoteDataSource
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.data.sync.SyncManager
import com.example.bootcamp.domain.model.PendingProfile
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
    private val tokenManager: TokenManager,
    private val pendingProfileDao: PendingProfileDao,
    private val userProfileCacheDao: UserProfileCacheDao,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) : UserProfileRepository {

    override suspend fun submitProfile(request: UserProfileRequest): Result<UserProfile> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Not authenticated"))

            // Try remote first if online
            if (networkMonitor.isConnected) {
                val response = remoteDataSource.submitProfile(token, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        val dto = apiResponse.data
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
                        
                        return Result.success(profile)
                    }
                }
                // If remote fails, fall through to queue locally
            }

            // Queue for offline sync
            val pendingProfile = PendingProfileEntity(
                address = request.address,
                nik = request.nik,
                ktpPath = request.ktpPath,
                phoneNumber = request.phoneNumber,
                accountNumber = request.accountNumber,
                bankName = request.bankName,
                syncStatus = SyncStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )
            pendingProfileDao.insert(pendingProfile)
            syncManager.scheduleProfileSync()

            // Return a "queued" profile for UI display
            Result.success(UserProfile(
                username = "",
                email = "",
                address = request.address,
                nik = request.nik,
                ktpPath = request.ktpPath,
                phoneNumber = request.phoneNumber,
                accountNumber = request.accountNumber,
                bankName = request.bankName,
                updatedAt = null,
                isPending = true
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadKtp(imageUri: Uri): Result<String> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Not authenticated"))

            val response = remoteDataSource.uploadKtp(token, imageUri)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    Result.success(apiResponse.data.ktpPath ?: "")
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Failed to upload KTP"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to upload KTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Not authenticated"))

            // If online, fetch from remote and cache
            if (networkMonitor.isConnected) {
                val response = remoteDataSource.getUserProfile(token)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        val dto = apiResponse.data
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
                        
                        return Result.success(profile)
                    } else {
                        // Remote returned error, try cache
                        return getCachedProfile()
                            ?: Result.failure(Exception(apiResponse?.message ?: "Failed to get profile"))
                    }
                } else {
                    // HTTP error, try cache
                    return getCachedProfile()
                        ?: Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get profile"))
                }
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
        userProfileCacheDao.insertOrUpdate(entity)
    }

    private suspend fun getCachedProfile(): Result<UserProfile>? {
        val cached = userProfileCacheDao.getProfile() ?: return null
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

    override fun getPendingProfile(): Flow<PendingProfile?> {
        return pendingProfileDao.observePendingProfile().map { entity ->
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
    }

    override suspend fun retryPendingProfile(): Result<Unit> {
        return try {
            val profile = pendingProfileDao.getPendingProfile()
                ?: return Result.failure(IllegalArgumentException("No pending profile found"))

            pendingProfileDao.update(profile.copy(
                syncStatus = SyncStatus.PENDING,
                retryCount = 0,
                errorMessage = null
            ))
            syncManager.scheduleProfileSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearPendingProfile(): Result<Unit> {
        return try {
            pendingProfileDao.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Clear cached profile data (e.g., on logout). */
    suspend fun clearCache() {
        userProfileCacheDao.clear()
    }
}

