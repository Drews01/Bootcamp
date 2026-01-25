package com.example.bootcamp.data.remote.interceptor

import android.util.Log
import com.example.bootcamp.data.remote.cookie.PersistentCookieJar
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the X-XSRF-TOKEN header to mutable requests (POST, PUT, DELETE, PATCH). The
 * token is retrieved from the PersistentCookieJar and URL-decoded to ensure raw value is sent.
 */
@Singleton
class CsrfInterceptor @Inject constructor(private val cookieJar: PersistentCookieJar) :
        Interceptor {

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
            !path.contains("/auth/forgot-password")
        ) {
            val rawXsrfToken = cookieJar.getXsrfToken()

            if (rawXsrfToken != null) {
                // URL-decode the token in case it was URL-encoded in the cookie
                val decodedToken = try {
                    URLDecoder.decode(rawXsrfToken, "UTF-8")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to URL-decode XSRF token, using raw value: ${e.message}")
                    rawXsrfToken
                }
                
                Log.d(TAG, "Raw XSRF token: $rawXsrfToken")
                Log.d(TAG, "Decoded XSRF token: $decodedToken")
                Log.d(TAG, "Adding X-XSRF-TOKEN header for: $method $fullUrl")
                
                val newRequest = originalRequest.newBuilder()
                    .header("X-XSRF-TOKEN", decodedToken)
                    .build()
                    
                // Log all headers for debugging
                Log.d(TAG, "Request headers: ${newRequest.headers}")
                
                return chain.proceed(newRequest)
            } else {
                Log.e(TAG, "XSRF-TOKEN cookie not found! Request to $path will likely fail with 403.")
                Log.e(TAG, "Make sure to call /api/csrf-token endpoint first or ensure login sets the cookie.")
            }
        }

        return chain.proceed(originalRequest)
    }
}
