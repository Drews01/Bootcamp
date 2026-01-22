package com.example.bootcamp.data.remote.cookie

import android.util.Log
import com.example.bootcamp.data.local.TokenManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * A simpler PersistentCookieJar that keeps cookies in memory during the session and interacts with
 * TokenManager (DataStore) to persist strictly the XSRF-TOKEN.
 */
@Singleton
class PersistentCookieJar @Inject constructor(private val tokenManager: TokenManager) : CookieJar {

    private val cookieStore = HashMap<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
            cookieStore[url.host] = cookies
            // Find XSRF-TOKEN and persist it
            cookies.find { it.name == "XSRF-TOKEN" }?.let { xsrfCookie ->
                Log.d("PersistentCookieJar", "Persisting XSRF-TOKEN: ${xsrfCookie.value}")
                // Using runBlocking here to ensure persistence.
                // Since this runs on a background network thread appropriately, it is safe.
                try {
                    runBlocking { tokenManager.saveXsrfToken(xsrfCookie.value) }
                } catch (e: Exception) {
                    Log.e("PersistentCookieJar", "Failed to save XSRF token", e)
                }
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: emptyList()
    }

    /**
     * Helper to retrieve XSRF-TOKEN specifically for the Interceptor. Checks memory first, then
     * DataStore if missing.
     */
    fun getXsrfToken(): String? {
        // 1. Try memory
        val memoryToken = cookieStore.values.flatten().find { it.name == "XSRF-TOKEN" }?.value
        if (memoryToken != null) return memoryToken

        // 2. Try persistence (lazy load)
        return try {
            val persistedToken = runBlocking { tokenManager.xsrfToken.firstOrNull() }
            if (persistedToken != null) {
                Log.d("PersistentCookieJar", "Loaded XSRF-TOKEN from storage: $persistedToken")
            }
            persistedToken
        } catch (e: Exception) {
            Log.e("PersistentCookieJar", "Failed to load XSRF token", e)
            null
        }
    }

    fun clear() {
        cookieStore.clear()
    }
}
