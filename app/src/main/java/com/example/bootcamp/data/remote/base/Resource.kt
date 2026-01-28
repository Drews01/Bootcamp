package com.example.bootcamp.data.remote.base

/**
 * A sealed class that represents the state of a resource (data). Used for handling loading,
 * success, and error states in a clean way. Follows the single source of truth principle for UI
 * state.
 *
 * @param T The type of data being wrapped
 */
sealed class Resource<out T> {

    /** Represents a loading state, optionally with cached data. */
    data class Loading<T>(val data: T? = null) : Resource<T>()

    /** Represents a successful state with data. */
    data class Success<T>(val data: T) : Resource<T>()

    /** Represents an error state with message and optional cached data. */
    data class Error<T>(
        val message: String,
        val data: T? = null,
        val errorDetails: ErrorDetails? = null,
        val exception: Throwable? = null
    ) : Resource<T>()

    /** Returns true if this is a Loading state. */
    val isLoading: Boolean
        get() = this is Loading

    /** Returns true if this is a Success state. */
    val isSuccess: Boolean
        get() = this is Success

    /** Returns true if this is an Error state. */
    val isError: Boolean
        get() = this is Error

    /** Get the data regardless of state, or null if not available. */
    fun getDataOrNull(): T? = when (this) {
        is Loading -> data
        is Success -> data
        is Error -> data
    }

    /** Get error details if in error state. */
    fun getErrorDetailsOrNull(): ErrorDetails? = (this as? Error)?.errorDetails

    /** Get field error for specific field if in error state. */
    fun getFieldError(fieldName: String): String? = getErrorDetailsOrNull()?.getFieldError(fieldName)

    /** Map the data to a different type. */
    fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Loading -> Loading(data?.let(transform))
        is Success -> Success(transform(data))
        is Error -> Error(message, data?.let(transform), errorDetails, exception)
    }

    companion object {
        /** Create a Loading resource. */
        fun <T> loading(data: T? = null): Resource<T> = Loading(data)

        /** Create a Success resource. */
        fun <T> success(data: T): Resource<T> = Success(data)

        /** Create an Error resource. */
        fun <T> error(
            message: String,
            data: T? = null,
            errorDetails: ErrorDetails? = null,
            exception: Throwable? = null
        ): Resource<T> = Error(message, data, errorDetails, exception)
    }
}
