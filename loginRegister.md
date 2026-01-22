# Authentication Feature Documentation

This document describes the authentication features implemented in the STAR Financial Bootcamp Android app.

## Overview

The app includes a complete authentication flow with:
- **Login** - Email/username and password authentication
- **Register** - New user registration with validation
- **Forgot Password** - Password reset request via email
- **Home Screen** - Public homepage with conditional auth UI

## API Endpoints

All endpoints use base URL: `http://localhost:8081/` (maps to `10.0.2.2:8081` on Android emulator)

### 1. Register New User
```
POST /auth/register
```

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Validation:**
- `username`: required, 3-50 characters
- `email`: required, valid email format
- `password`: required, minimum 8 characters

### 2. Login User
```
POST /auth/login
```

**Request Body:**
```json
{
  "usernameOrEmail": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "jwt-token-here",
  "message": "Login successful"
}
```

### 3. Forgot Password
```
POST /auth/forgot-password
```

**Request Body:**
```json
{
  "email": "john@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset email sent"
}
```

### 4. Reset Password
```
POST /auth/reset-password
```

**Request Body:**
```json
{
  "token": "reset-token-from-email",
  "newPassword": "newPassword123"
}
```

### 5. Logout
```
POST /auth/logout
```

**Headers:**
```
Authorization: Bearer <jwt-token>
```

---

## Screen Descriptions

### Home Screen
- **Route:** `/home` (start destination)
- **Features:**
  - STAR Financial branding with Indigo gradient
  - Conditional UI based on authentication state:
    - **Not logged in:** Shows "Sign In" button
    - **Logged in:** Shows username and "Logout" button
  - Accessible without authentication

### Login Screen
- **Route:** `/login`
- **Features:**
  - Email/username and password input fields
  - Password visibility toggle
  - "Forgot Password?" link
  - "Sign Up" link for new users
  - Loading state with circular progress indicator
  - Error/success message display

### Register Screen
- **Route:** `/register`
- **Features:**
  - Username field (3-50 characters validation)
  - Email field (format validation)
  - Password field (8+ characters validation)
  - Confirm password field (match validation)
  - Real-time client-side validation
  - Back navigation to Login

### Forgot Password Screen
- **Route:** `/forgot_password`
- **Features:**
  - Email input with validation
  - "Send Reset Link" button
  - Success message for confirmation
  - Back navigation to Login

---

## Project Structure & File Documentation

The project follows **Clean Architecture** principles with separation between data, domain, and presentation layers.

```
app/src/main/java/com/example/bootcamp/
├── MainActivity.kt
├── data/
│   ├── api/
│   │   ├── AuthService.kt
│   │   └── RetrofitClient.kt
│   ├── local/
│   │   └── TokenManager.kt
│   ├── model/
│   │   └── AuthModels.kt
│   └── repository/
│       └── AuthRepository.kt
└── ui/
    ├── navigation/
    │   └── Navigation.kt
    ├── screens/
    │   ├── ForgotPasswordScreen.kt
    │   ├── HomeScreen.kt
    │   ├── LoginScreen.kt
    │   └── RegisterScreen.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    └── viewmodel/
        └── AuthViewModel.kt
```

---

## Detailed File Documentation

### Root Level

#### `MainActivity.kt`
**Location:** `com.example.bootcamp/`  
**Purpose:** The single Activity entry point for the app.  
**Why here:** Android apps require at least one Activity. As a Compose-first app, this is the only Activity needed - it sets up the theme, initializes dependencies, and hosts the navigation graph.

---

### Data Layer (`data/`)

The `data/` package contains all data-related code following the Repository pattern. This separates data concerns from UI logic.

#### `data/model/AuthModels.kt`
**Location:** `data/model/`  
**Purpose:** Contains all data classes (DTOs) for API requests and responses.  
**Why here:** Models are pure data containers with no business logic. Grouping them in `model/` makes them easy to find and modify when API contracts change.

**Classes:**
- `RegisterRequest` - Body for POST /auth/register
- `LoginRequest` - Body for POST /auth/login
- `ForgotPasswordRequest` - Body for POST /auth/forgot-password
- `ResetPasswordRequest` - Body for POST /auth/reset-password
- `AuthResponse` - Response containing JWT token
- `ApiResponse` - Generic success/message response

---

#### `data/api/AuthService.kt`
**Location:** `data/api/`  
**Purpose:** Retrofit interface defining HTTP endpoints.  
**Why here:** API interfaces belong in `api/` to clearly indicate they're network-related. This follows Retrofit's convention of interface-based service definitions.

**Endpoints:**
- `register()` → POST /auth/register
- `login()` → POST /auth/login
- `forgotPassword()` → POST /auth/forgot-password
- `resetPassword()` → POST /auth/reset-password
- `logout()` → POST /auth/logout

---

#### `data/api/RetrofitClient.kt`
**Location:** `data/api/`  
**Purpose:** Singleton providing configured Retrofit instance.  
**Why here:** Client configuration (base URL, timeouts, interceptors) is API infrastructure, not business logic. Keeping it with `AuthService` ensures all network code is co-located.

**Configuration:**
- Base URL: `http://10.0.2.2:8081/` (emulator localhost)
- Logging interceptor for debugging
- 30-second timeouts
- Gson converter factory

---

#### `data/local/TokenManager.kt`
**Location:** `data/local/`  
**Purpose:** Manages JWT token persistence using DataStore.  
**Why here:** `local/` indicates local/persistent storage as opposed to remote (`api/`). This separation makes it clear where data comes from.

**Features:**
- Save/retrieve JWT token via Flow
- Save/retrieve username
- Clear all auth data on logout
- Uses Preferences DataStore (not Room) for simple key-value storage

---

#### `data/repository/AuthRepository.kt`
**Location:** `data/repository/`  
**Purpose:** Single source of truth for authentication data and operations.  
**Why here:** Repositories abstract data sources from the rest of the app. The ViewModel doesn't know if data comes from network, cache, or database - it just asks the repository.

**Responsibilities:**
- Coordinates between `AuthService` (remote) and `TokenManager` (local)
- Handles error mapping from HTTP responses to Result<T>
- Saves token on successful login
- Clears token on logout

---

### UI Layer (`ui/`)

The `ui/` package contains all presentation logic following MVVM architecture with Jetpack Compose.

#### `ui/theme/Color.kt`
**Location:** `ui/theme/`  
**Purpose:** Defines the app's color palette.  
**Why here:** Theme files are standard Compose convention. Colors are used throughout all screens, so centralizing them ensures consistency.

**Colors defined:** Indigo (primary), Green (success), Red (error), Amber (warning), Blue (info), Gray (neutral), accent colors.

---

#### `ui/viewmodel/AuthViewModel.kt`
**Location:** `ui/viewmodel/`  
**Purpose:** Manages UI state and handles user actions for authentication.  
**Why here:** ViewModels bridge the data and UI layers. Keeping them in `viewmodel/` shows they're presentation-layer components, not data or domain logic.

**State managed:**
- `isLoading` - Show loading indicators
- `isLoggedIn` - Authentication status
- `username` - Current user
- `errorMessage` / `successMessage` - Feedback to user

**Actions:**
- `login()`, `register()`, `forgotPassword()`, `logout()`
- `clearMessages()` - Reset feedback state

---

#### `ui/screens/LoginScreen.kt`
**Location:** `ui/screens/`  
**Purpose:** Composable for the login form UI.  
**Why here:** Each major screen gets its own file in `screens/`. This makes navigation targets obvious and keeps files focused.

**Features:**
- Email/username + password fields
- Password visibility toggle
- Links to Register and Forgot Password
- Loading state, error/success messages

---

#### `ui/screens/RegisterScreen.kt`
**Location:** `ui/screens/`  
**Purpose:** Composable for new user registration.  
**Why here:** Same as LoginScreen - one file per screen for clarity.

**Features:**
- Username, email, password, confirm password fields
- Real-time client-side validation
- Back navigation to Login

---

#### `ui/screens/ForgotPasswordScreen.kt`
**Location:** `ui/screens/`  
**Purpose:** Composable for password reset request.  
**Why here:** Follows screen-per-file convention.

**Features:**
- Email input with validation
- Success confirmation message
- Back navigation

---

#### `ui/screens/HomeScreen.kt`
**Location:** `ui/screens/`  
**Purpose:** Main landing page with conditional auth UI.  
**Why here:** This is the start destination of navigation.

**Features:**
- Shows Login button when not authenticated
- Shows username + Logout button when authenticated
- STAR Financial branding

---

#### `ui/navigation/Navigation.kt`
**Location:** `ui/navigation/`  
**Purpose:** Defines the navigation graph using Navigation Compose.  
**Why here:** Navigation is a cross-cutting UI concern. Separating it from screens makes routes easy to find and modify.

**Routes:**
- `home` - Start destination
- `login` - Login screen
- `register` - Registration screen
- `forgot_password` - Password reset

---

## Why This Structure?

| Pattern | Benefit |
|---------|---------|
| **data/model/** | All DTOs in one place, easy API contract changes |
| **data/api/** | Network code isolated, easy to swap HTTP clients |
| **data/local/** | Local storage separate from remote, clear data flow |
| **data/repository/** | Single source of truth, abstracts data sources |
| **ui/viewmodel/** | MVVM pattern, testable presentation logic |
| **ui/screens/** | One screen per file, clear navigation targets |
| **ui/navigation/** | Centralized routing, easy to add new screens |
| **ui/theme/** | Consistent styling, easy theme changes |

This structure scales well as the app grows and makes it easy for new developers to understand where code belongs.

---
