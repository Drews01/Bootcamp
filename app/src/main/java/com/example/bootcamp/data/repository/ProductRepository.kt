package com.example.bootcamp.data.repository

import com.example.bootcamp.data.remote.api.UserProductService
import com.example.bootcamp.data.remote.base.ApiResponse
import com.example.bootcamp.data.remote.dto.UserTierLimitDTO
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val userProductService: UserProductService
) {

    suspend fun fetchUserTier(): Result<UserTierLimitDTO?> {
        return try {
            val response = userProductService.getUserTier()
            if (response.isSuccessful) {
                // If body is null or empty, it might mean "No Product", let's handle that by returning null success.
                // However, based on the prompt: "Returns 200 OK with null or empty body (depending on frontend parsing) if the user has no active product."
                // Typically Retrofit return body as null if empty.
                val body = response.body()
                if (body != null && body.data != null) {
                    Result.success(body.data)
                } else {
                     // 200 OK but empty/null data => User has no active product
                    Result.success(null)
                }
            } else {
                 // Try to parse error body if needed, or just return failure
                Result.failure(Exception("Error fetching tier: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
