package com.example.bootcamp.data.repository

import android.net.Uri
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.remote.datasource.UserProfileRemoteDataSource
import com.example.bootcamp.data.remote.dto.UserProfileRequest
import com.example.bootcamp.domain.model.UserProfile
import com.example.bootcamp.domain.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

/** Implementation of UserProfileRepository. */
@Singleton
class UserProfileRepositoryImpl
@Inject
constructor(
        private val remoteDataSource: UserProfileRemoteDataSource,
        private val tokenManager: TokenManager
) : UserProfileRepository {

    override suspend fun submitProfile(request: UserProfileRequest): Result<UserProfile> {
        return try {
            val token =
                    tokenManager.token.first()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response = remoteDataSource.submitProfile(token, request)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    val dto = apiResponse.data
                    val profile =
                            UserProfile(
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
                    Result.success(profile)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Failed to submit profile"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to submit profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadKtp(imageUri: Uri): Result<String> {
        return try {
            val token =
                    tokenManager.token.first()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response = remoteDataSource.uploadKtp(token, imageUri)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    Result.success(apiResponse.data.ktpPath)
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
            val token =
                tokenManager.token.first()
                    ?: return Result.failure(Exception("Not authenticated"))

            val response = remoteDataSource.getUserProfile(token)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    val dto = apiResponse.data
                    val profile =
                        UserProfile(
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
                    Result.success(profile)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Failed to get profile"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to get profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
