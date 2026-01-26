package com.example.bootcamp.data.remote.api

import com.example.bootcamp.data.remote.base.ApiResponse
import com.example.bootcamp.data.remote.dto.UserTierLimitDTO
import retrofit2.Response
import retrofit2.http.GET

interface UserProductService {
    @GET("api/user-products/my-tier")
    suspend fun getUserTier(): Response<ApiResponse<UserTierLimitDTO>>
}
