package com.example.bootcamp.domain.repository

import android.net.Uri
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.domain.model.UserProfile

/** Repository interface for user profile operations. */
interface UserProfileRepository {

    /**
     * Submit/update user profile.
     * @param request Profile data to submit
     * @return Result containing UserProfile on success or failure
     */
    suspend fun submitProfile(request: UserProfileRequest): Result<UserProfile>

    /**
     * Upload KTP image.
     * @param imageUri URI of the image to upload
     * @return Result containing the KTP path on success or failure
     */
    suspend fun uploadKtp(imageUri: Uri): Result<String>
}
