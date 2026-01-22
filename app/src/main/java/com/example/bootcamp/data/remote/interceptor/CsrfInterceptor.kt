package com.example.bootcamp.data.remote.interceptor

import android.util.Log
import com.example.bootcamp.data.remote.cookie.PersistentCookieJar
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the X-XSRF-TOKEN header to mutable requests (POST, PUT, DELETE, PATCH). The
 * token is retrieved from the PersistentCookieJar.
 */
@Singleton
class CsrfInterceptor @Inject constructor(private val cookieJar: PersistentCookieJar) :
        Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val method = originalRequest.method

        val path = originalRequest.url.encodedPath

        // Check if method is mutable and not in excluded list
        if ((method == "POST" || method == "PUT" || method == "DELETE" || method == "PATCH") &&
            !path.contains("/auth/login") &&
            !path.contains("/auth/register") &&
            !path.contains("/auth/forgot-password")
        ) {
            val xsrfToken = cookieJar.getXsrfToken()

            if (xsrfToken != null) {
                Log.d("CsrfInterceptor", "Adding X-XSRF-TOKEN header: $xsrfToken")
                val newRequest =
                        originalRequest.newBuilder().header("X-XSRF-TOKEN", xsrfToken).build()
                return chain.proceed(newRequest)
            } else {
                Log.w("CsrfInterceptor", "XSRF-TOKEN cookie not found! Request might fail.")
            }
        }

        return chain.proceed(originalRequest)
    }
}
