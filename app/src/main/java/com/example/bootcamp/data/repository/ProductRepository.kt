package com.example.bootcamp.data.repository

import com.example.bootcamp.data.local.dao.UserTierDao
import com.example.bootcamp.data.local.entity.UserTierEntity
import com.example.bootcamp.data.remote.api.UserProductService
import com.example.bootcamp.data.remote.dto.UserTierLimitDTO
import com.example.bootcamp.util.NetworkMonitor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val userProductService: UserProductService,
    private val userTierDao: UserTierDao,
    private val networkMonitor: NetworkMonitor
) {

    suspend fun fetchUserTier(): Result<UserTierLimitDTO?> {
        // If online, fetch from remote and cache
        if (networkMonitor.isConnected) {
            return try {
                val response = userProductService.getUserTier()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        // Cache the tier data
                        val dto = body.data
                        val entity = UserTierEntity(
                            tierName = dto.tierName,
                            tierCode = dto.tierCode,
                            creditLimit = dto.creditLimit,
                            currentUsedAmount = dto.currentUsedAmount,
                            availableCredit = dto.availableCredit,
                            totalPaidAmount = dto.totalPaidAmount,
                            upgradeThreshold = dto.upgradeThreshold,
                            remainingToUpgrade = dto.remainingToUpgrade,
                            status = dto.status
                        )
                        userTierDao.insertOrUpdate(entity)
                        Result.success(dto)
                    } else {
                        // 200 OK but empty/null data => User has no active product
                        Result.success(null)
                    }
                } else {
                    // Remote failed, try cache fallback
                    getCachedTier()
                        ?: Result.failure(Exception("Error fetching tier: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                // Network error, try cache fallback
                getCachedTier() ?: Result.failure(e)
            }
        }

        // Offline - return from cache
        return getCachedTier()
            ?: Result.failure(Exception("No cached tier data available. Please connect to the internet."))
    }

    private suspend fun getCachedTier(): Result<UserTierLimitDTO>? {
        val cached = userTierDao.getUserTier() ?: return null
        val dto = UserTierLimitDTO(
            tierName = cached.tierName,
            tierCode = cached.tierCode,
            creditLimit = cached.creditLimit,
            currentUsedAmount = cached.currentUsedAmount,
            availableCredit = cached.availableCredit,
            totalPaidAmount = cached.totalPaidAmount,
            upgradeThreshold = cached.upgradeThreshold,
            remainingToUpgrade = cached.remainingToUpgrade,
            status = cached.status
        )
        return Result.success(dto)
    }

    /** Clear cached tier data (e.g., on logout). */
    suspend fun clearCache() {
        userTierDao.clear()
    }
}
