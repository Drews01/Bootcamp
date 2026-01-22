package com.example.bootcamp.data.remote.base

import com.google.gson.annotations.SerializedName

/**
 * Standardized API response wrapper matching backend ApiResponse<T>. All API endpoints return this
 * structure.
 *
 * @param T The type of data in the response
 */
data class ApiResponse<T>(
        @SerializedName("success") val success: Boolean = false,
        @SerializedName("message") val message: String? = null,
        @SerializedName("data") val data: T? = null,
        @SerializedName("error") val error: ErrorDetails? = null,
        @SerializedName("timestamp") val timestamp: String? = null, // ISO format, for logging only
        @SerializedName("statusCode") val statusCode: Int? = null
) {
    /** Check if the response indicates success. */
    fun isSuccess(): Boolean = success && error == null

    /** Check if the response has data. */
    fun hasData(): Boolean = data != null

    /** Get error message or default. */
    fun getErrorMessage(): String = error?.getDisplayMessage() ?: message ?: "Unknown error"
}
