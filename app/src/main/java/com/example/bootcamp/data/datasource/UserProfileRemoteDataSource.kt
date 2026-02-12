package com.example.bootcamp.data.datasource

import android.net.Uri
import com.example.bootcamp.data.remote.dto.KtpUploadResponse
import com.example.bootcamp.data.remote.dto.UserProfileDto
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.util.ApiResult

/**
 * Interface for user profile remote data source.
 * Defines contract for all profile-related network operations.
 */
interface UserProfileRemoteDataSource {
    suspend fun submitProfile(token: String, request: UserProfileRequest): ApiResult<UserProfileDto>
    suspend fun uploadKtp(token: String, imageUri: Uri): ApiResult<KtpUploadResponse>
    suspend fun getUserProfile(token: String): ApiResult<UserProfileDto>
}
