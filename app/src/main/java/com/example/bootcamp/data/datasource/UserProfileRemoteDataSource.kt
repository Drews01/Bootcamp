package com.example.bootcamp.data.datasource

import android.net.Uri
import com.example.bootcamp.data.remote.base.ApiResponse
import com.example.bootcamp.data.remote.dto.KtpUploadResponse
import com.example.bootcamp.data.remote.dto.UserProfileDto
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import retrofit2.Response

/**
 * Interface for user profile remote data source.
 * Defines contract for all profile-related network operations.
 */
interface UserProfileRemoteDataSource {
    suspend fun submitProfile(token: String, request: UserProfileRequest): Response<ApiResponse<UserProfileDto>>
    suspend fun uploadKtp(token: String, imageUri: Uri): Response<ApiResponse<KtpUploadResponse>>
    suspend fun getUserProfile(token: String): Response<ApiResponse<UserProfileDto>>
}
