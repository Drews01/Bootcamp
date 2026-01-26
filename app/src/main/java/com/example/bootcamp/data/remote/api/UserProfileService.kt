package com.example.bootcamp.data.remote.api

import com.example.bootcamp.data.remote.base.ApiResponse
import com.example.bootcamp.data.remote.dto.KtpUploadResponse
import com.example.bootcamp.data.remote.dto.UserProfileDto
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/** Retrofit service interface for user profile API endpoints. */
interface UserProfileService {

    /**
     * Submit/update user profile.
     * @param token Authorization token
     * @param request Profile data
     * @return ApiResponse with UserProfileDto on success
     */
    @POST("api/user-profiles")
    suspend fun submitProfile(
            @Header("Authorization") token: String,
            @Body request: UserProfileRequest
    ): Response<ApiResponse<UserProfileDto>>

    /**
     * Upload KTP image.
     * @param token Authorization token
     * @param file The image file as multipart
     * @return ApiResponse with KtpUploadResponse containing the file path
     */
    @Multipart
    @POST("api/user-profiles/upload-ktp")
    suspend fun uploadKtp(
            @Header("Authorization") token: String,
            @Part file: MultipartBody.Part
    ): Response<ApiResponse<KtpUploadResponse>>

    @GET("api/user-profiles/me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserProfileDto>>
}
