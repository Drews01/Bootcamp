package com.example.bootcamp.data.remote.cookie

import android.content.Context
import android.util.Log
import com.example.bootcamp.data.local.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
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
class PersistentCookieJar @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager
) : CookieJar {

    private val cookieStore = HashMap<String, MutableList<Cookie>>()
    private val sharedPreferences = context.getSharedPreferences("api_cookies", Context.MODE_PRIVATE)
    private val gson = com.google.gson.Gson()

    init {
        loadAllCookies()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
            val host = url.host
            val existingCookies = cookieStore[host] ?: mutableListOf()
            
            // Update or add new cookies
            for (newCookie in cookies) {
                Log.d("PersistentCookieJar", "Received cookie: ${newCookie.name} = ${newCookie.value.take(20)}...")
                val index = existingCookies.indexOfFirst { it.name == newCookie.name }
                if (index != -1) {
                    existingCookies[index] = newCookie
                } else {
                    existingCookies.add(newCookie)
                }
            }
            cookieStore[host] = existingCookies
            persistCookies(host, existingCookies)
            
            // NOTE: We no longer save XSRF-TOKEN cookie value to TokenManager.
            // The masked token from GET /api/csrf-token response body is used instead (BREACH protection).
            // The cookie is still sent automatically by OkHttp's CookieJar for server-side validation.
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        Log.d("PersistentCookieJar", "loadForRequest called for host: ${url.host}, path: ${url.encodedPath}")
        Log.d("PersistentCookieJar", "Available hosts in cookieStore: ${cookieStore.keys}")
        
        val cookies = cookieStore[url.host] ?: run {
            Log.w("PersistentCookieJar", "No cookies found for host: ${url.host}")
            return emptyList()
        }
        
        // Filter expired cookies
        val validCookies = cookies.filter { it.expiresAt >= System.currentTimeMillis() }
        
        if (validCookies.size != cookies.size) {
            Log.d("PersistentCookieJar", "Removed ${cookies.size - validCookies.size} expired cookies")
            // Some cookies expired, update store
            cookieStore[url.host] = validCookies.toMutableList()
            persistCookies(url.host, validCookies)
        }
        
        // Log all cookies being sent
        validCookies.forEach { cookie ->
            Log.d("PersistentCookieJar", "Sending cookie: ${cookie.name}=${cookie.value.take(20)}...")
        }
        
        return validCookies
    }



    private fun loadAllCookies() {
        val allEntries = sharedPreferences.all
        for ((host, value) in allEntries) {
            if (value is String) {
                try {
                    val type = object : com.google.gson.reflect.TypeToken<List<Cookie>>() {}.type
                    // Note: Gson might not deserialize Cookie correctly because it has no no-arg constructor and custom fields.
                    // However, if it fails, we need a custom TypeAdapter. 
                    // Let's assume for now standard serialization might encounter issues with OkHttp's Cookie class 
                    // because it uses a Builder pattern and might not have fields exposed easily for Gson.
                    // A safer bet is to use a SerializableCookie wrapper or custom serializer.
                    // BUT, to keep it simple first, let's try direct. 
                    // ACTUALLY, Cookie class in OkHttp is not easily Gson-serializable (private fields, no setters).
                    // We should use a simplified data class or custom serializer.
                    // Let's implement a custom serializer/deserializer approach implicitly by creating a SerializableCookie DTO if needed.
                    // OR, better, let's check if we can reconstruct it.
                    // Since I cannot easily add complex inner classes here without inflating the code too much, 
                    // I will check if I can use a simpler storage format or just try Gson.
                    // Wait, OkHttp Cookie object structure:
                    // It has `name`, `value`, `expiresAt`, `domain`, `path`, `secure`, `httpOnly`, etc.
                    // Gson generally needs fields.
                    
                    // Let's switch to a custom simpler serialization to be safe and robust without external TypeAdapters.
                    // Or stick to Gson but verify if Cookie works. 'Cookie' has private fields.
                    // Better approach: Store as List<SerializableCookie> where SerializableCookie is specific DTO.
                    // To avoid creating a new file, I will keep it internal here if possible, or use a workaround.
                    
                    // Alternative: Serialize a custom object that maps to Cookie attributes.
                    
                    val storedCookies: List<PersistableCookie> = gson.fromJson(value, object : com.google.gson.reflect.TypeToken<List<PersistableCookie>>() {}.type)
                    val okHttpCookies = storedCookies.map { it.toCookie() }
                    cookieStore[host] = okHttpCookies.toMutableList()
                } catch (e: Exception) {
                    Log.e("PersistentCookieJar", "Failed to load cookies for $host", e)
                }
            }
        }
    }

    /**
     * Helper to retrieve XSRF-TOKEN specifically for the Interceptor. Checks memory first, then
     * DataStore if missing. Also ensures that if loaded from DataStore, the cookie is reconstructed
     * so it can be sent with the request.
     */
    fun getXsrfToken(): String? {
        Log.d("PersistentCookieJar", "getXsrfToken() called")
        Log.d("PersistentCookieJar", "cookieStore hosts: ${cookieStore.keys}")
        
        // 1. Try memory - look for XSRF-TOKEN in any host's cookies
        for ((host, cookies) in cookieStore) {
            val xsrfCookie = cookies.find { it.name == "XSRF-TOKEN" }
            if (xsrfCookie != null) {
                Log.d("PersistentCookieJar", "Found XSRF-TOKEN in memory for host: $host, value: ${xsrfCookie.value.take(20)}...")
                return xsrfCookie.value
            }
        }
        
        Log.d("PersistentCookieJar", "XSRF-TOKEN not found in memory, checking DataStore...")

        // 2. Try persistence (lazy load from TokenManager)
        return try {
            val persistedToken = runBlocking { tokenManager.xsrfToken.firstOrNull() }
            if (persistedToken != null) {
                Log.d("PersistentCookieJar", "Loaded XSRF-TOKEN from DataStore: ${persistedToken.take(20)}...")
                
                // CRITICAL FIX: Reconstruct the cookie and add it to cookieStore
                // so that loadForRequest() will include it in the Cookie header
                val existingHost = cookieStore.keys.firstOrNull()
                if (existingHost != null) {
                    Log.d("PersistentCookieJar", "Reconstructing XSRF-TOKEN cookie for host: $existingHost")
                    val reconstructedCookie = Cookie.Builder()
                        .name("XSRF-TOKEN")
                        .value(persistedToken)
                        .domain(existingHost)
                        .path("/")
                        .expiresAt(System.currentTimeMillis() + 24 * 60 * 60 * 1000) // 24 hours
                        .httpOnly()
                        .build()
                    
                    val existingCookies = cookieStore[existingHost] ?: mutableListOf()
                    existingCookies.add(reconstructedCookie)
                    cookieStore[existingHost] = existingCookies
                    Log.d("PersistentCookieJar", "Added reconstructed XSRF-TOKEN cookie to memory")
                } else {
                    Log.w("PersistentCookieJar", "No existing host in cookieStore to attach XSRF cookie to")
                }
            } else {
                Log.w("PersistentCookieJar", "No XSRF-TOKEN found in DataStore either!")
            }
            persistedToken
        } catch (e: Exception) {
            Log.e("PersistentCookieJar", "Failed to load XSRF token", e)
            null
        }
    }

    /**
     * Ensure XSRF cookie exists for a given host. Call this to sync token from DataStore to cookie.
     */
    fun ensureXsrfCookieForHost(host: String) {
        val existingXsrf = cookieStore[host]?.find { it.name == "XSRF-TOKEN" }
        if (existingXsrf != null) {
            Log.d("PersistentCookieJar", "XSRF cookie already exists for $host")
            return
        }
        
        // Try to load from DataStore and create cookie
        try {
            val persistedToken = runBlocking { tokenManager.xsrfToken.firstOrNull() }
            if (persistedToken != null) {
                Log.d("PersistentCookieJar", "Creating XSRF cookie for $host from DataStore")
                val cookie = Cookie.Builder()
                    .name("XSRF-TOKEN")
                    .value(persistedToken)
                    .domain(host)
                    .path("/")
                    .expiresAt(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                    .httpOnly()
                    .build()
                
                val cookies = cookieStore[host] ?: mutableListOf()
                cookies.add(cookie)
                cookieStore[host] = cookies
            }
        } catch (e: Exception) {
            Log.e("PersistentCookieJar", "Failed to ensure XSRF cookie for $host", e)
        }
    }

    fun clear() {
        cookieStore.clear()
        sharedPreferences.edit().clear().apply()
    }
    
    // Simple DTO for JSON serialization
    private data class PersistableCookie(
        val name: String,
        val value: String,
        val expiresAt: Long,
        val domain: String,
        val path: String,
        val secure: Boolean,
        val httpOnly: Boolean,
        val hostOnly: Boolean
    ) {
        fun toCookie(): Cookie {
            val builder = Cookie.Builder()
                .name(name)
                .value(value)
                .expiresAt(expiresAt)
                .path(path)
            
            if (hostOnly) {
                builder.hostOnlyDomain(domain)
            } else {
                builder.domain(domain)
            }
            
            if (secure) builder.secure()
            if (httpOnly) builder.httpOnly()
            
            return builder.build()
        }
    }

    private fun Cookie.toPersistable(): PersistableCookie {
        return PersistableCookie(
            name = name,
            value = value,
            expiresAt = expiresAt,
            domain = domain,
            path = path,
            secure = secure,
            httpOnly = httpOnly,
            hostOnly = hostOnly
        )
    }
    
    // Override persistCookies to use DTO
    private fun persistCookies(host: String, cookies: List<Cookie>) {
        val persistableCookies = cookies.map { it.toPersistable() }
        val json = gson.toJson(persistableCookies)
        sharedPreferences.edit().putString(host, json).apply()
    }
}
