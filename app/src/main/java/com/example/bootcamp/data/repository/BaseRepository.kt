package com.example.bootcamp.data.repository

import com.example.bootcamp.data.remote.base.ApiException
import com.example.bootcamp.util.ApiResult

/**
 * Base repository class to standardize error handling and result mapping.
 */
abstract class BaseRepository {

    /**
     * Maps ApiResult<T> to Result<R> with a transformation function.
     */
    protected suspend inline fun <T, R> mapApiResult(
        result: ApiResult<T>,
        crossinline transform: suspend (T) -> R
    ): Result<R> = when (result) {
        is ApiResult.Success -> {
            try {
                Result.success(transform(result.data))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        is ApiResult.Error -> {
            Result.failure(
                ApiException(
                    message = result.message,
                    errorDetails = result.errorDetails,
                    statusCode = result.statusCode
                )
            )
        }
    }

    /**
     * Maps ApiResult<T> to Result<T> (identity transformation).
     * Non-suspend version for simple mapping without transformation.
     */
    protected fun <T> mapApiResult(result: ApiResult<T>): Result<T> = when (result) {
        is ApiResult.Success -> {
            Result.success(result.data)
        }
        is ApiResult.Error -> {
            Result.failure(
                ApiException(
                    message = result.message,
                    errorDetails = result.errorDetails,
                    statusCode = result.statusCode
                )
            )
        }
    }
}
