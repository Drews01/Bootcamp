package com.example.bootcamp.util

import android.util.Log
import com.example.bootcamp.data.remote.base.ApiException
import com.example.bootcamp.data.remote.base.ApiResponse
import com.example.bootcamp.data.remote.base.ErrorDetails
import retrofit2.Response

/**
 * Sealed class representing the result of an API call. Provides type-safe success and error
 * handling.
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
            val message: String,
            val errorDetails: ErrorDetails? = null,
            val statusCode: Int? = null,
            val exception: Throwable? = null
    ) : ApiResult<Nothing>()

    val isSuccess: Boolean
        get() = this is Success
    val isError: Boolean
        get() = this is Error

    /** Get data if success, or null if error. */
    fun getOrNull(): T? = (this as? Success)?.data

    /** Get error details if error. */
    fun errorOrNull(): ErrorDetails? = (this as? Error)?.errorDetails

    /** Map success data to another type. */
    fun <R> map(transform: (T) -> R): ApiResult<R> =
            when (this) {
                is Success -> Success(transform(data))
                is Error -> this
            }

    /** Execute action on success. */
    inline fun onSuccess(action: (T) -> Unit): ApiResult<T> {
        if (this is Success) action(data)
        return this
    }

    /** Execute action on error. */
    inline fun onError(action: (Error) -> Unit): ApiResult<T> {
        if (this is Error) action(this)
        return this
    }

    /** Convert to Kotlin Result. */
    fun toResult(): Result<T> =
            when (this) {
                is Success -> Result.success(data)
                is Error -> Result.failure(ApiException(message, errorDetails, statusCode))
            }

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(
                message: String,
                errorDetails: ErrorDetails? = null,
                statusCode: Int? = null,
                exception: Throwable? = null
        ) = Error(message, errorDetails, statusCode, exception)
    }
}

/** Utility object for handling API responses. */
object ApiResponseHandler {

    private const val TAG = "ApiResponseHandler"
    
    // Use this for debug logging - will be true for debug builds
    private val isDebugBuild: Boolean
        get() = android.os.Build.TYPE == "userdebug" || android.os.Build.TYPE == "eng" || true  // Set to false in production

    /**
     * Safely execute an API call and return ApiResult.
     *
     * @param apiCall The suspend function that makes the API call
     * @return ApiResult with success data or error details
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): ApiResult<T> {
        return try {
            val response = apiCall()
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}", e)
            ApiResult.error(message = e.message ?: "Network error", exception = e)
        }
    }

    /** Handle the Retrofit response and convert to ApiResult. */
    private fun <T> handleResponse(response: Response<ApiResponse<T>>): ApiResult<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                if (body.isSuccess() && body.data != null) {
                    // Log timestamp for debugging
                    if (isDebugBuild) {
                        Log.d(TAG, "API success at: ${body.timestamp}")
                    }
                    ApiResult.success(body.data)
                } else if (body.isSuccess() && body.data == null) {
                    // Success but no data (e.g., logout, delete operations)
                    @Suppress("UNCHECKED_CAST") ApiResult.success(Unit as T)
                } else {
                    // Backend returned success=false
                    logError(body)
                    ApiResult.error(
                            message = body.getErrorMessage(),
                            errorDetails = body.error,
                            statusCode = body.statusCode
                    )
                }
            } else {
                ApiResult.error(message = "Empty response body", statusCode = response.code())
            }
        } else {
            // HTTP error (4xx, 5xx)
            val errorBody = parseErrorBody(response)
            ApiResult.error(
                    message = errorBody?.getErrorMessage()
                                    ?: "HTTP ${response.code()}: ${response.message()}",
                    errorDetails = errorBody?.error,
                    statusCode = response.code()
            )
        }
    }

    /** Parse error body from failed response. */
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseErrorBody(response: Response<T>): ApiResponse<Any>? {
        return try {
            val errorString = response.errorBody()?.string()
            if (!errorString.isNullOrEmpty()) {
                com.google.gson.Gson().fromJson(errorString, ApiResponse::class.java) as?
                        ApiResponse<Any>
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse error body: ${e.message}")
            null
        }
    }

    /** Log error details for debugging. */
    private fun <T> logError(body: ApiResponse<T>) {
        if (isDebugBuild) {
            Log.e(TAG, "API error at ${body.timestamp}")
            Log.e(TAG, "Error code: ${body.error?.errorCode}")
            Log.e(TAG, "Message: ${body.message}")
            body.error?.fieldErrors?.forEach { (field, error) ->
                Log.e(TAG, "Field error - $field: $error")
            }
            body.error?.errors?.forEach { error -> Log.e(TAG, "General error: $error") }
            body.error?.stackTrace?.let { stackTrace -> Log.e(TAG, "Stack trace: $stackTrace") }
        }
    }
}

/** Extension function to convert ApiResult to Result for UseCases. */
fun <T> ApiResult<T>.asResult(): Result<T> =
        when (this) {
            is ApiResult.Success -> Result.success(data)
            is ApiResult.Error -> Result.failure(ApiException(message, errorDetails, statusCode))
        }

/** Extension function to get field errors map from ApiResult.Error. */
fun ApiResult.Error.getFieldErrors(): Map<String, String>? = errorDetails?.fieldErrors
