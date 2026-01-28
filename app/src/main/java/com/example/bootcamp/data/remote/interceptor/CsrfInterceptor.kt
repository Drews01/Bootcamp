package com.example.bootcamp.data.remote.interceptor

import android.util.Log
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.remote.api.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Interceptor that fetches a fresh CSRF token before each mutable request (POST, PUT, DELETE, PATCH).
 *
 * IMPORTANT: The CSRF token is SINGLE-USE. This interceptor calls GET /api/csrf-token
 * right before each protected request to get a fresh masked token. This adds a few milliseconds
 * but guarantees the request will work.
 *
 * The masked token from the response body is used for the X-XSRF-TOKEN header (BREACH protection).
 */
@Singleton
class CsrfInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val retrofitProvider: Provider<Retrofit>
) : Interceptor {

    companion object {
        private const val TAG = "CsrfInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val method = originalRequest.method
        val path = originalRequest.url.encodedPath
        val fullUrl = originalRequest.url.toString()

        Log.d(TAG, "Processing request: $method $path")

        // Check if method is mutable and not in excluded list
        if ((method == "POST" || method == "PUT" || method == "DELETE" || method == "PATCH") &&
            !path.contains("/auth/login") &&
            !path.contains("/auth/register") &&
            !path.contains("/auth/forgot-password") &&
            !path.contains("/api/csrf-token") // Don't add CSRF header when fetching the token
        ) {
            // Fetch a FRESH CSRF token before each protected request (token is single-use)
            val maskedToken = fetchFreshCsrfToken()

            if (maskedToken != null) {
                Log.d(TAG, "Using FRESH MASKED CSRF token: ${maskedToken.take(30)}...")
                Log.d(TAG, "Adding X-XSRF-TOKEN header for: $method $fullUrl")

                val newRequest = originalRequest.newBuilder()
                    .header("X-XSRF-TOKEN", maskedToken)
                    .build()

                Log.d(TAG, "Request headers: ${newRequest.headers}")

                return chain.proceed(newRequest)
            } else {
                Log.e(TAG, "Failed to fetch fresh CSRF token!")
                Log.e(TAG, "Request to $path will likely fail with 403.")
            }
        }

        return chain.proceed(originalRequest)
    }

    /**
     * Fetch a fresh CSRF token by calling GET /api/csrf-token.
     * Returns the masked token from the response body, or null on failure.
     */
    private fun fetchFreshCsrfToken(): String? {
        return runBlocking {
            try {
                Log.d(TAG, "Fetching fresh CSRF token...")

                val retrofit = retrofitProvider.get()
                val authService = retrofit.create(AuthService::class.java)

                val response = authService.getCsrfToken()

                if (response.isSuccessful) {
                    // Response is direct CsrfTokenData, not wrapped in ApiResponse
                    val csrfTokenData = response.body()
                    val maskedToken = csrfTokenData?.token

                    if (maskedToken != null) {
                        Log.d(TAG, "Got fresh CSRF token: ${maskedToken.take(30)}...")
                        // Also store it in TokenManager for potential future use
                        tokenManager.saveXsrfToken(maskedToken)
                        return@runBlocking maskedToken
                    } else {
                        Log.e(TAG, "CSRF token response body is null or token is missing")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch CSRF token: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching CSRF token", e)
            }
            null
        }
    }
}
