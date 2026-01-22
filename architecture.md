# Bootcamp App Architecture Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Project Structure](#project-structure)
4. [Architecture Layers](#architecture-layers)
5. [Dependency Injection](#dependency-injection)
6. [Data Flow](#data-flow)
7. [Best Practices Implemented](#best-practices-implemented)
8. [Firebase Integration](#firebase-integration)
9. [Setup Guide](#setup-guide)

---

## Overview

The Bootcamp application follows **Clean Architecture** principles with **MVVM** (Model-View-ViewModel) presentation pattern. The architecture ensures:

- ✅ **Separation of Concerns** - Each layer has a single responsibility
- ✅ **Testability** - Dependencies are injected, easily mockable
- ✅ **Maintainability** - Clean boundaries between layers
- ✅ **Scalability** - Easy to add new features
- ✅ **Offline-First** - Local caching with Room database

### Technology Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Architecture | Clean Architecture + MVVM |
| DI Framework | Hilt |
| Local Database | Room |
| Preferences | DataStore |
| Networking | Retrofit + OkHttp |
| Async | Kotlin Coroutines + Flow |
| Push Notifications | Firebase Cloud Messaging |

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Screens   │  │  Components │  │      ViewModels         │  │
│  │ (Composable)│  │ (Reusable)  │  │    (@HiltViewModel)     │  │
│  └──────┬──────┘  └─────────────┘  └───────────┬─────────────┘  │
│         │                                       │                 │
│         └───────────────────┬───────────────────┘                 │
│                             │ observes StateFlow                  │
└─────────────────────────────┼─────────────────────────────────────┘
                              │
┌─────────────────────────────┼─────────────────────────────────────┐
│                      DOMAIN LAYER                                  │
│                             │                                      │
│  ┌──────────────────────────▼──────────────────────────────────┐  │
│  │                      UseCases                                │  │
│  │   LoginUseCase | RegisterUseCase | LogoutUseCase | etc.     │  │
│  └──────────────────────────┬──────────────────────────────────┘  │
│                             │                                      │
│  ┌──────────────────────────▼──────────────────────────────────┐  │
│  │              Repository Interfaces                           │  │
│  │                   AuthRepository                             │  │
│  └──────────────────────────┬──────────────────────────────────┘  │
│                             │                                      │
│  ┌──────────────────────────┴──────────────────────────────────┐  │
│  │               Domain Models (User, etc.)                     │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┼─────────────────────────────────────┘
                              │
┌─────────────────────────────┼─────────────────────────────────────┐
│                       DATA LAYER                                   │
│                             │                                      │
│  ┌──────────────────────────▼──────────────────────────────────┐  │
│  │              Repository Implementations                      │  │
│  │                  AuthRepositoryImpl                          │  │
│  └────────────┬─────────────────────────────────┬──────────────┘  │
│               │                                 │                  │
│  ┌────────────▼────────────┐    ┌───────────────▼──────────────┐  │
│  │     LOCAL (Room)        │    │        REMOTE                │  │
│  │  ┌─────────────────┐    │    │    ┌─────────────────────┐   │  │
│  │  │   AppDatabase   │    │    │    │ AuthRemoteDataSource│   │  │
│  │  │   UserDao       │    │    │    └──────────┬──────────┘   │  │
│  │  │   UserEntity    │    │    │               │              │  │
│  │  └─────────────────┘    │    │    ┌──────────▼──────────┐   │  │
│  │  ┌─────────────────┐    │    │    │    AuthService      │   │  │
│  │  │  TokenManager   │    │    │    │    (Retrofit)       │   │  │
│  │  │  (DataStore)    │    │    │    └─────────────────────┘   │  │
│  │  └─────────────────┘    │    │    ┌─────────────────────┐   │  │
│  │                         │    │    │    DTOs             │   │  │
│  │  │  (DataStore)    │    │    │    └─────────────────────┘   │  │
│  │  └─────────────────┘    │    │                              │  │
│  └─────────────────────────┘    └──────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
app/src/main/java/com/example/bootcamp/
│
├── BootcampApplication.kt          # Hilt Application class
├── MainActivity.kt                 # Main entry point (@AndroidEntryPoint)
│
├── di/                             # Dependency Injection Modules
│   ├── AppModule.kt               # DataStore, TokenManager
│   ├── NetworkModule.kt           # Retrofit, OkHttp, AuthService
│   ├── DatabaseModule.kt          # Room database, DAOs
│   └── RepositoryModule.kt        # Repository bindings
│
├── data/                           # Data Layer
│   ├── local/                     # Local data sources
│   │   ├── database/
│   │   │   └── AppDatabase.kt     # Room database
│   │   ├── dao/
│   │   │   └── UserDao.kt         # Data Access Object
│   │   ├── entity/
│   │   │   └── UserEntity.kt      # Room entity
│   │   └── TokenManager.kt        # DataStore preferences
│   │
│   ├── remote/                    # Remote data sources
│   │   ├── api/
│   │   │   └── AuthService.kt     # Retrofit API interface
│   │   ├── datasource/            # Remote Data Sources
│   │   │   └── AuthRemoteDataSource.kt
│   │   ├── base/                  # Base response classes
│   │   │   ├── ApiResponse.kt     # Standardized API wrapper
│   │   │   ├── ErrorDetails.kt    # Error structure + ErrorCode
│   │   │   ├── ApiException.kt    # Custom API exception
│   │   │   └── Resource.kt        # UI state wrapper
│   │   └── dto/
│   │       └── AuthDto.kt         # Request/Response DTOs
│   │
│   ├── repository/                # Repository implementations
│   │   └── AuthRepositoryImpl.kt
│   │
│   └── mapper/                    # Entity ↔ Domain mappers
│       └── UserMapper.kt
│
├── domain/                         # Domain Layer
│   ├── model/                     # Domain models
│   │   └── User.kt
│   │
│   ├── repository/                # Repository interfaces
│   │   └── AuthRepository.kt
│   │
│   └── usecase/                   # Business logic
│       ├── base/
│       │   └── UseCase.kt         # Base UseCase interfaces
│       └── auth/
│           ├── LoginUseCase.kt
│           ├── RegisterUseCase.kt
│           ├── LogoutUseCase.kt
│           ├── ForgotPasswordUseCase.kt
│           └── GetAuthStateUseCase.kt
│
├── ui/                             # Presentation Layer
│   ├── components/                # Reusable UI components
│   │   ├── buttons/
│   │   │   ├── PrimaryButton.kt
│   │   │   └── SecondaryButton.kt
│   │   ├── inputs/
│   │   │   ├── EmailTextField.kt
│   │   │   └── PasswordTextField.kt
│   │   ├── cards/
│   │   │   └── GlassCard.kt
│   │   └── loading/
│   │       └── LoadingOverlay.kt
│   │
│   ├── screens/                   # Screen composables
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   └── HomeScreen.kt
│   │
│   ├── viewmodel/                 # ViewModels
│   │   └── AuthViewModel.kt
│   │
│   ├── navigation/                # Navigation
│   │   └── Navigation.kt
│   │
│   └── theme/                     # Theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── firebase/                       # Firebase Integration
│   ├── BootcampFirebaseMessagingService.kt
│   └── NotificationHelper.kt
│
└── util/                           # Utilities
    ├── ApiResponseHandler.kt      # Safe API call wrapper
    ├── NetworkMonitor.kt          # Connectivity observer
    └── Constants.kt               # App constants
```

---

## Architecture Layers

### 1. Presentation Layer (`ui/`)

The UI layer contains all Compose UI code and ViewModels.

**Components:**
- **Screens** - Full-screen composables (LoginScreen, HomeScreen, etc.)
- **Components** - Reusable UI elements (PrimaryButton, GlassCard, etc.)
- **ViewModels** - State holders using `StateFlow` and `@HiltViewModel`

**Key Principles:**
- ViewModels expose `StateFlow<UiState>` to screens
- Screens are stateless, receiving state from ViewModel
- Components are reusable and composable

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    // ...
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
}
```

### 2. Domain Layer (`domain/`)

The domain layer contains business logic, independent of any framework.

**Components:**
- **Models** - Pure Kotlin data classes representing business entities
- **Repository Interfaces** - Contracts for data operations
- **UseCases** - Single-purpose business logic units

**Key Principles:**
- No Android dependencies (pure Kotlin)
- Each UseCase has a single responsibility
- Repository interfaces allow flexible implementations

```kotlin
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCaseWithParams<LoginParams, Result<String>> {
    
    override suspend fun invoke(params: LoginParams): Result<String> {
        // Validation logic
        if (params.usernameOrEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("..."))
        }
        return authRepository.login(params.usernameOrEmail, params.password)
    }
}
```

### 3. Data Layer (`data/`)

The data layer handles data operations from various sources.

**Components:**
- **Remote** - Network operations (Retrofit services, DTOs)
- **DataSources** - Encapsulate remote/local data access (e.g., `AuthRemoteDataSource`)
- **Local** - Local storage (Room database, DataStore)
- **Repository Implementations** - Coordinate data sources
- **Mappers** - Convert between layers

**Key Principles:**
- Repository is single source of truth
- DataSource handles raw data fetching/saving
- Offline-first: check cache before network
- DTOs for network, Entities for database, Domain models for business

```kotlin
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager
) : AuthRepository {
    // Implementation
}
```

---

## Dependency Injection

Hilt is used for dependency injection with the following modules:

### AppModule
Provides app-wide dependencies:
- `DataStore<Preferences>`
- `TokenManager`

### NetworkModule
Provides networking dependencies:
- `HttpLoggingInterceptor`
- `OkHttpClient`
- `Retrofit`
- `AuthService`

### DatabaseModule
Provides database dependencies:
- `AppDatabase`
- `UserDao`

### RepositoryModule
Binds repository implementations:
- `AuthRepository` → `AuthRepositoryImpl`

---

## Data Flow

### Login Flow Step-by-Step

**1. UI Interaction (Screen)**
   - **File:** [`LoginScreen.kt`](file:///c:/Users/Andrew/AndroidStudioProjects/Bootcamp/app/src/main/java/com/example/bootcamp/ui/screens/LoginScreen.kt)
   - User enters email: `user@example.com` and password: `password123`.
   - Clicks "Login" button.
   - `LoginScreen` calls `viewModel.login("user@example.com", "password123")`.

**2. ViewModel (State Management)**
   - **File:** [`AuthViewModel.kt`](file:///c:/Users/Andrew/AndroidStudioProjects/Bootcamp/app/src/main/java/com/example/bootcamp/ui/viewmodel/AuthViewModel.kt)
   - `AuthViewModel` sets `isLoading = true` (UI shows loading spinner).
   - Launches coroutine and calls `LoginUseCase`.

**3. Domain Layer (Business Logic)**
   - **File:** [`LoginUseCase.kt`](file:///c:/Users/Andrew/AndroidStudioProjects/Bootcamp/app/src/main/java/com/example/bootcamp/domain/usecase/auth/LoginUseCase.kt)
   - `LoginUseCase` validates email format and password length.
   - Calls `authRepository.login(email, password)`.

**4. Data Layer (Repository & RemoteDataSource)**
   - **Files:**
     - [`AuthRepositoryImpl.kt`](file:///c:/Users/Andrew/AndroidStudioProjects/Bootcamp/app/src/main/java/com/example/bootcamp/data/repository/AuthRepositoryImpl.kt)
     - [`AuthRemoteDataSource.kt`](file:///c:/Users/Andrew/AndroidStudioProjects/Bootcamp/app/src/main/java/com/example/bootcamp/data/remote/datasource/AuthRemoteDataSource.kt)
     - [`AuthService.kt`](file:///c:/Users/Andrew/AndroidStudioProjects/Bootcamp/app/src/main/java/com/example/bootcamp/data/remote/api/AuthService.kt)
   - `AuthRepositoryImpl` delegates to `AuthRemoteDataSource.login()`.
   - `AuthRemoteDataSource` makes safe network call using `AuthService`.
   - **API Request Sent:**
     ```json
     POST /api/auth/login
     {
       "usernameOrEmail": "user@example.com",
       "password": "password123"
     }
     ```

**5. API Response (Remote)**
   - **API Success Response:**
     ```json
     {
       "success": true,
       "message": "Login successful",
       "data": {
         "token": "eyJhbGciOiJIUzI1...",
         "userId": "user_123",
         "username": "User",
         "email": "user@example.com"
       },
       "statusCode": 200
     }
     ```

**6. Data Processing & Storage**
   - `AuthRemoteDataSource` returns `ApiResult.Success(LoginData)`.
   - `AuthRepositoryImpl` receives success.
   - **Local Storage:** Repository calls `TokenManager.saveUserData()`:
     - **File:** [`TokenManager.kt`](file:///c:/Users/Andrew/AndroidStudioProjects/Bootcamp/app/src/main/java/com/example/bootcamp/data/local/TokenManager.kt)
     - Saves `jwt_token` to DataStore (Preferences).
     - Saves `user_id`, `username`, `email` to DataStore.
   - Repository returns `Result.success("Login successful")`.

**7. UI Update**
   - `AuthViewModel` receives success result.
   - Updates state: `isLoading = false`, `isLoggedIn = true`, `successMessage = "Login successful"`.
   - `LoginScreen` observes `isLoggedIn = true` and navigates to `HomeScreen`.

---

## Best Practices Implemented

### ✅ Clean Architecture
- Clear separation between UI, Domain, and Data layers
- Dependencies point inward (UI → Domain ← Data)

### ✅ MVVM Pattern
- ViewModels manage UI state
- Unidirectional data flow
- StateFlow for reactive updates

### ✅ SOLID Principles
- **S**ingle Responsibility: Each UseCase does one thing
- **O**pen/Closed: Extend via new UseCases, not modification
- **L**iskov Substitution: Repository interface allows any implementation
- **I**nterface Segregation: Small, focused interfaces
- **D**ependency Inversion: Depend on abstractions (interfaces)

### ✅ Hilt Dependency Injection
- All dependencies injected, not manually created
- Easy to mock for testing
- Scoped appropriately (@Singleton, @HiltViewModel)

### ✅ Room Database
- Type-safe database operations
- Flow-based reactive queries
- Entity-to-Domain mapping

### ✅ Coroutines & Flow
- Suspend functions for async operations
- StateFlow for UI state
- Flow for reactive data streams

### ✅ Offline-First Ready
- Local caching with Room
- NetworkMonitor for connectivity
- Resource sealed class for state handling

### ✅ Reusable Components
- PrimaryButton, SecondaryButton
- EmailTextField, PasswordTextField
- GlassCard, LoadingOverlay

### ✅ Firebase Push Notifications
- BootcampFirebaseMessagingService
- NotificationHelper with channels

---

## API Response Handling

### File Structure

```
data/remote/base/
├── ApiResponse.kt      # Main response wrapper
├── ErrorDetails.kt     # Error structure + ErrorCode constants
├── ApiException.kt     # Custom exception for API errors
└── Resource.kt         # UI state wrapper (Loading/Success/Error)
```

### ApiResponse Structure

All API endpoints return a standardized `ApiResponse<T>` wrapper:

```kotlin
// Located in: data/remote/base/ApiResponse.kt
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val error: ErrorDetails?,
    val timestamp: String?,    // ISO format, for logging
    val statusCode: Int?
)
```

### ErrorDetails Structure

```kotlin
// Located in: data/remote/base/ErrorDetails.kt
data class ErrorDetails(
    val errorCode: String?,
    val fieldErrors: Map<String, String>?,  // Field-specific validation errors
    val errors: List<String>?,               // General error list
    val stackTrace: String?,                 // Debug only
    val additionalInfo: Map<String, Any>?    // Contains rootCause for DB errors
)

object ErrorCode {
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val USER_NOT_FOUND = "USER_NOT_FOUND"
    const val DUPLICATE_ENTRY = "DUPLICATE_ENTRY"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
}
```

### Error Codes

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| VALIDATION_ERROR | 400 | Form validation failed |
| INVALID_CREDENTIALS | 401 | Wrong username/password |
| USER_NOT_FOUND | 404 | User doesn't exist |
| DUPLICATE_ENTRY | 409 | Email/username already exists |
| INTERNAL_ERROR | 500 | Unexpected server error |

### ApiResult Sealed Class

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val message: String,
        val errorDetails: ErrorDetails?,
        val statusCode: Int?
    ) : ApiResult<Nothing>()
}
```

### Field Error Handling in UI

```kotlin
// In ViewModel
val fieldErrors = errorDetails?.fieldErrors ?: emptyMap()
_uiState.update { it.copy(fieldErrors = fieldErrors) }

// In Composable
val emailError = uiState.getFieldError("email")
OutlinedTextField(
    value = email,
    isError = emailError != null,
    supportingText = { emailError?.let { Text(it) } }
)
```

---

## Security Architecture

### CSRF Protection Strategy
The app uses a **Double Submit Cookie** + **Sync Token** hybrid pattern adapted for mobile.

| Component | Responsibility | Pattern |
|-----------|----------------|---------|
| **PersistentCookieJar** | Captures and persists cookies (HttpOnly `accessToken` + `XSRF-TOKEN`). Uses `DataStore` for persistence. | Repository |
| **CsrfInterceptor** | Intercepts `POST`/`PUT`/`DELETE` requests. Reads `XSRF-TOKEN` cookie and attaches it as `X-XSRF-TOKEN` header. | Interceptor |
| **TokenManager** | securely handles the storage of the XSRF token string for valid persistence. | Local Data Source |

### Security Data Flow
```
1. Login Request -> Server returns Set-Cookie: XSRF-TOKEN=...
2. PersistentCookieJar -> Saves token to Memory & DataStore
3. User performs Action (e.g. Update Profile)
4. CsrfInterceptor -> Reads XSRF-TOKEN
5. CsrfInterceptor -> Adds Header: X-XSRF-TOKEN: ...
6. Server -> Verifies Header matches Cookie
```

---

## Firebase Integration

### Setup Steps

1. **Create Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use existing one
   - Add an Android app with package name `com.example.bootcamp`

2. **Download google-services.json**
   - Download from Firebase Console
   - Place in `app/` directory

3. **Request Notification Permission (Android 13+)**
   ```kotlin
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
       requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
   }
   ```

4. **Get FCM Token**
   ```kotlin
   FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
       if (task.isSuccessful) {
           val token = task.result
           // Send to your backend
       }
   }
   ```

### Notification Channels

| Channel ID | Name | Priority |
|------------|------|----------|
| default_channel | General | Default |
| high_priority_channel | Important | High |
| transactions_channel | Transactions | High |

---

## Setup Guide

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11+
- Kotlin 2.0+

### Steps

1. **Clone the repository**

2. **Add google-services.json**
   - Create Firebase project
   - Download and place in `app/` folder

3. **Update API Base URL**
   - Edit `di/NetworkModule.kt`
   - Change `BASE_URL` to your backend URL

4. **Sync and Build**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Run the app**

---

## Testing

### Unit Testing
```kotlin
@Test
fun `login with empty username returns failure`() = runTest {
    val useCase = LoginUseCase(mockRepository)
    val result = useCase(LoginParams("", "password"))
    assertTrue(result.isFailure)
}
```

### UI Testing
```kotlin
@HiltAndroidTest
class LoginScreenTest {
    @Test
    fun loginButton_displaysWhenFieldsFilled() {
        // Compose UI test
    }
}
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2026-01-22 | Initial architecture implementation |

---

*Documentation generated as part of the Bootcamp architecture refactoring.*
