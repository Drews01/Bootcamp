package com.example.bootcamp.data.remote.base

import com.google.gson.annotations.SerializedName

/**
 * Error details structure matching backend ErrorDetails. Contains error code, field-specific
 * errors, and general error lists.
 */
data class ErrorDetails(
        @SerializedName("errorCode") val errorCode: String? = null,
        @SerializedName("fieldErrors") val fieldErrors: Map<String, String>? = null,
        @SerializedName("errors") val errors: List<String>? = null,
        @SerializedName("stackTrace")
        val stackTrace: String? = null, // Only populated in debug builds
        @SerializedName("additionalInfo") val additionalInfo: Map<String, Any>? = null
) {
    /**
     * Get error for a specific field (for TextInputLayout.error).
     * @param fieldName The field name (e.g., "email", "password", "username")
     * @return Error message for the field, or null if no error
     */
    fun getFieldError(fieldName: String): String? = fieldErrors?.get(fieldName)

    /** Check if there are any field-specific errors. */
    fun hasFieldErrors(): Boolean = !fieldErrors.isNullOrEmpty()

    /** Check if this is a validation error. */
    fun isValidationError(): Boolean = errorCode == ErrorCode.VALIDATION_ERROR

    /** Check if this is an authentication error. */
    fun isAuthError(): Boolean =
            errorCode in
                    listOf(
                            ErrorCode.INVALID_CREDENTIALS,
                            ErrorCode.UNAUTHORIZED,
                            ErrorCode.TOKEN_EXPIRED
                    )

    /** Get root cause from additionalInfo (for database errors). */
    fun getRootCause(): String? = additionalInfo?.get("rootCause") as? String

    /**
     * Get the best message to display to the user. Priority: fieldErrors summary > first general
     * error > errorCode
     */
    fun getDisplayMessage(): String {
        return when {
            hasFieldErrors() -> fieldErrors!!.values.first()
            !errors.isNullOrEmpty() -> errors.first()
            !getRootCause().isNullOrEmpty() -> getRootCause()!!
            else -> errorCode ?: "An error occurred"
        }
    }

    /** Get all error messages as a single string. */
    fun getAllErrorsAsString(): String {
        val allErrors = mutableListOf<String>()
        fieldErrors?.values?.let { allErrors.addAll(it) }
        errors?.let { allErrors.addAll(it) }
        return allErrors.joinToString("\n")
    }
}

/** Error codes matching backend ErrorCode enum. */
object ErrorCode {
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val USER_NOT_FOUND = "USER_NOT_FOUND"
    const val DUPLICATE_ENTRY = "DUPLICATE_ENTRY"
    const val INVALID_ARGUMENT = "INVALID_ARGUMENT"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val TOKEN_EXPIRED = "TOKEN_EXPIRED"
    const val FORBIDDEN = "FORBIDDEN"
    const val NOT_FOUND = "NOT_FOUND"
}
