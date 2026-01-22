package com.example.bootcamp.util

/** Application-wide constants. */
object Constants {

    /** Base URL for API calls. TODO: Update this with your actual API URL. */
    const val BASE_URL = "https://your-api-base-url.com/"

    /** Network timeout in seconds. */
    const val NETWORK_TIMEOUT = 30L

    /** Cache validity duration in milliseconds (1 hour). */
    const val CACHE_VALIDITY_MS = 60 * 60 * 1000L

    /** DataStore preference keys. */
    object Preferences {
        const val PREFERENCES_NAME = "app_preferences"
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_USERNAME = "username"
        const val KEY_USER_ID = "user_id"
    }

    /** Navigation routes. */
    object Routes {
        const val HOME = "home"
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val FORGOT_PASSWORD = "forgot_password"
    }
}
