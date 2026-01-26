# Comprehensive Android CSRF Protection Documentation

This document provides a deep dive into the **BREACH-resistant CSRF protection** implementation. It covers the **what**, **where**, **why**, and **how** of every component, including architectural decisions (Clean Architecture), design patterns (Singleton, Dependency Injection), and syntax explanations.

---

## 1. Project Structure & File Locations

We follow **Clean Architecture** combined with the **Repository Pattern**. This separates concerns into layers: Data, Domain, and UI.

### Map of CSRF Components

| Component | File Path | Layer | Purpose |
| :--- | :--- | :--- | :--- |
| **AuthService** | `app/src/main/java/com/example/bootcamp/data/remote/api/AuthService.kt` | **Data (Remote)** | Defines the raw HTTP API endpoints using Retrofit annotations. |
| **CsrfTokenData** | `app/src/main/java/com/example/bootcamp/data/remote/dto/AuthDto.kt` | **Data (DTO)** | **Data Transfer Object**. Defines the JSON structure returned by the server. |
| **TokenManager** | `app/src/main/java/com/example/bootcamp/data/local/TokenManager.kt` | **Data (Local)** | Manages local storage (DataStore) for persisting tokens securely. |
| **PersistentCookieJar**| `app/src/main/java/com/example/bootcamp/data/remote/cookie/PersistentCookieJar.kt` | **Data (Remote)** | Handles HTTP Cookies (the *Session*). Standard CookieJar implementation. |
| **CsrfInterceptor** | `app/src/main/java/com/example/bootcamp/data/remote/interceptor/CsrfInterceptor.kt` | **Data (Remote)** | **Middleware**. Intercepts outgoing requests to attach the `X-XSRF-TOKEN` header. |
| **NetworkModule** | `app/src/main/java/com/example/bootcamp/di/NetworkModule.kt` | **DI (Hilt)** | **Dependency Injection**. Wires everything together and provides instances to the app. |

---

## 2. Component Deep Dive

### A. AuthService (`data/remote/api/AuthService.kt`)

**Why here?**
Located in `data/remote/api` because it defines the *remote* interface to the backend. It's the contract between the app and the server.

**The Code:**
```kotlin
interface AuthService {
    @GET("api/csrf-token")
    suspend fun getCsrfToken(): Response<CsrfTokenData>
}
```

**Syntax & Logic Explanation:**
*   `interface`: Retrofit uses interfaces to generate API implementation code at runtime.
*   `@GET("...")`: Retrofit annotation. Tells HTTP client to perform a `GET` request to the specified relative URL.
*   `suspend`: Kotlin Coroutines keyword. Means this function is **asynchronous** and non-blocking. It must be called from a Coroutine or another suspend function.
*   `Response<T>`: A Retrofit wrapper. It gives us access not just to the body (T), but also HTTP status codes (200, 403, etc.), headers, and raw error bodies.
*   `CsrfTokenData`: We use a specific DTO because we want to parse the JSON body strictly.

---

### B. CsrfTokenData (`data/remote/dto/AuthDto.kt`)

**Why here?**
DTOs (Data Transfer Objects) live in `data/remote/dto`. They are simple classes meant *only* to hold data moving over the network. They don't have logic.

**The Code:**
```kotlin
data class CsrfTokenData(
    @SerializedName("token") val token: String, 
    @SerializedName("headerName") val headerName: String? = null
)
```

**Syntax & Logic Explanation:**
*   `data class`: Kotlin feature. Automatically generates `equals()`, `hashCode()`, `toString()`, and getters/setters. Perfect for holding data.
*   `@SerializedName("token")`: Gson annotation. Maps the JSON key `"token"` from the server response to the Kotlin property `val token`, even if naming conventions differed (e.g., `json_token` -> `token`).

---

### C. TokenManager (`data/local/TokenManager.kt`)

**Why here?**
Located in `data/local` because it deals with *local* persistence on the device (DataStore).

**Why Singleton?**
Annotated with `@Singleton`.
*   **Reason**: Use Singletons for data repositories or managers that maintain state or handle expensive resources (like opening a file/db). We only want **ONE** instance of `TokenManager` managing our secrets to avoid race conditions and memory overhead.

**The Code:**
```kotlin
@Singleton
class TokenManager @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val xsrfToken: Flow<String?> = dataStore.data.map { prefs -> prefs[XSRF_TOKEN_KEY] }
    
    suspend fun saveXsrfToken(token: String) {
        dataStore.edit { prefs -> prefs[XSRF_TOKEN_KEY] = token }
    }
}
```

**Syntax Explanation:**
*   `@Inject`: Hilt/Dagger annotation. Tells the DI container: "I know how to create this class. Use this constructor."
*   `Flow<T>`: Kotlin Coroutines reactive stream. It emits a new value whenever the data in DataStore changes. UI or Interceptors can "observe" this.
*   `map { ... }`: Transformation operator. Converts the raw `Preferences` object into just the specific string we want.

---

### D. CsrfInterceptor (`data/remote/interceptor/CsrfInterceptor.kt`)

**Why here?**
Located in `data/remote/interceptor`. Interceptors are middleware in the network stack. They sit between the app and the network.

**Why Singleton?**
It is stateless (mostly) but expensive to recreate or we want it consistent across the `OkHttpClient` instance. Since `OkHttpClient` is a Singleton, its interceptors usually are too.

**The Code (Simplified):**
```kotlin
@Singleton
class CsrfInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val retrofitProvider: Provider<Retrofit> // <--- Lazy Injection
) : Interceptor { ... }
```

**Why `Provider<Retrofit>`? (Circular Dependency Fix)**
*   ** The Problem**: `CsrfInterceptor` needs to call `AuthService` to fetch a token. `AuthService` is built by `Retrofit`. `Retrofit` needs `OkHttpClient`. `OkHttpClient` needs `CsrfInterceptor`.
*   **The Cycle**: Interceptor -> Retrofit -> Client -> Interceptor (Loop).
*   **The Fix**: `Provider<T>` allows us to inject a "factory" for `Retrofit`. We only call `retrofitProvider.get()` *inside* the function method, breaking the initialization cycle.

**The Logic (Fetch-Before-Write):**
1.  **Intercept**: `intercept(chain)` is called for every request.
2.  **Check**: If it's `POST`/`PUT`/`DELETE`...
3.  **Fetch**: We pause the request chain and call `fetchFreshCsrfToken()`.
    *   This makes a *synchronous* call to the server (`GET /api/csrf-token`).
    *   We parse the `response.body().token` (The **Masked Token**).
4.  **Attach**: We create a new request: `originalRequest.newBuilder().header("X-XSRF-TOKEN", maskedToken).build()`.
5.  **Proceed**: `chain.proceed(newRequest)`.

---

### E. NetworkModule (`di/NetworkModule.kt`)

**Why here?**
Located in `di` (Dependency Injection). modules tell Hilt *how* to provide instances of classes we don't own (like `Retrofit`, `OkHttpClient`) or how to configure interfaces.

**The Code:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideCsrfInterceptor(
        tokenManager: TokenManager,
        retrofitProvider: Provider<Retrofit>
    ): CsrfInterceptor {
        return CsrfInterceptor(tokenManager, retrofitProvider)
    }
}
```

**Syntax & Logic:**
*   `@Module`: Defines a Dagger/Hilt module.
*   `@InstallIn(SingletonComponent::class)`: These dependencies live as long as the Application.
*   `@Provides`: A factory function. Hilt calls this when it needs a `CsrfInterceptor`.
*   `retrofitProvider: Provider<Retrofit>`: Hilt automatically knows how to inject a Provider for any bound type.

---

## 3. The Logic Flow (The "Why")

### The BREACH Attack Problem
Standard CSRF protection checks a cookie. But if I can guess your cookie (via compression side-channels), I can impersonate you.

### The Solution: Masked Tokens
1.  **Session Cookie**: `XSRF-TOKEN` (Raw). The server sends this on login. It identifies your session. Side-channel vulnerable? Yes, but it's HttpOnly.
2.  **Request Header**: `X-XSRF-TOKEN` (Masked). This is the Raw Token XOR'd with a random salt.
    *   It changes on *every request* or every fetch.
    *   Because it changes, compression attacks can't solve it.

### The Handshake Sequence
When you click **"Submit Loan"**:

1.  **Frontend (App)**: `CsrfInterceptor` wakes up. "Wait, this is a POST request."
2.  **Fetch**: Interceptor asks Server: "Give me a fresh masked token." (`GET /api/csrf-token`)
3.  **Server**: Checks your Cookie. "Okay, you are User X. Here is `MaskedToken_123`."
4.  **App**: Takes `MaskedToken_123`. Puts it in header `X-XSRF-TOKEN`.
    *   *Note*: The Cookie `XSRF-TOKEN` is *also* sent automatically by CookieJar.
5.  **Server Validation**: 
    *   Reads Header: `MaskedToken_123`.
    *   Unmasks the header -> Result: `RawToken_ABC`.
    *   Reads Cookie: `RawToken_ABC`.
    *   **Match?** Yes -> Process Request (200 OK).

This ensures that the entity making the request (The App) actually has the ability to read the response from the server (to get the masked token), proving it's not a blind attack from a malicious site.
