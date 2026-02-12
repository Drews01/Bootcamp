package com.example.bootcamp.data.remote.datasource

import android.content.Context
import android.net.Uri
import com.example.bootcamp.data.datasource.UserProfileRemoteDataSource
import com.example.bootcamp.data.remote.api.UserProfileService
import com.example.bootcamp.data.remote.dto.KtpUploadResponse
import com.example.bootcamp.data.remote.dto.UserProfileDto
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.util.ApiResponseHandler
import com.example.bootcamp.util.ApiResult
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/** Remote data source implementation for user profile operations. */
@Singleton
class UserProfileRemoteDataSourceImpl
@Inject
constructor(
    private val userProfileService: UserProfileService,
    @ApplicationContext private val context: Context
) : UserProfileRemoteDataSource {

    override suspend fun submitProfile(token: String, request: UserProfileRequest): ApiResult<UserProfileDto> =
        ApiResponseHandler.safeApiCall {
            userProfileService.submitProfile("Bearer $token", request)
        }

    override suspend fun uploadKtp(token: String, imageUri: Uri): ApiResult<KtpUploadResponse> =
        ApiResponseHandler.safeApiCall {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"

            // Create a temporary file from the URI
            val inputStream =
                contentResolver.openInputStream(imageUri)
                    ?: throw IllegalArgumentException("Cannot open image URI")

            val tempFile = File.createTempFile("ktp_upload", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { outputStream -> inputStream.copyTo(outputStream) }
            inputStream.close()

            val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

            userProfileService.uploadKtp("Bearer $token", multipartBody)
        }

    override suspend fun getUserProfile(token: String): ApiResult<UserProfileDto> =
        ApiResponseHandler.safeApiCall { userProfileService.getUserProfile("Bearer $token") }
}
