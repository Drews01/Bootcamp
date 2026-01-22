package com.example.bootcamp.data.remote.base

/**
 * Custom exception for API errors with structured error details. Thrown when API calls fail with
 * error response.
 */
class ApiException(
        override val message: String,
        val errorDetails: ErrorDetails? = null,
        val statusCode: Int? = null
) : Exception(message) {

    /** Get field error for specific field. */
    fun getFieldError(fieldName: String): String? = errorDetails?.getFieldError(fieldName)

    /** Get all field errors. */
    fun getFieldErrors(): Map<String, String>? = errorDetails?.fieldErrors

    /** Check if this is a validation error. */
    fun isValidationError(): Boolean = errorDetails?.isValidationError() == true

    /** Check if this is an authentication error. */
    fun isAuthError(): Boolean = errorDetails?.isAuthError() == true

    /** Get the error code. */
    fun getErrorCode(): String? = errorDetails?.errorCode

    /** Get root cause for database errors. */
    fun getRootCause(): String? = errorDetails?.getRootCause()

    companion object {
        /** Create ApiException from error details. */
        fun fromErrorDetails(
                message: String,
                errorDetails: ErrorDetails?,
                statusCode: Int?
        ): ApiException {
            return ApiException(message, errorDetails, statusCode)
        }

        /** Create ApiException for network errors. */
        fun networkError(message: String = "Network error"): ApiException {
            return ApiException(message)
        }

        /** Create ApiException for unknown errors. */
        fun unknownError(message: String = "Unknown error"): ApiException {
            return ApiException(message)
        }
    }
}
