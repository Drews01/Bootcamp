package com.example.bootcamp.security

import com.example.bootcamp.BuildConfig

/**
 * centralized security configuration.
 * Accesses sensitive values injected via BuildConfig from local.properties.
 * Keeps hardcoded secrets out of source code.
 */
object SecurityConfig {
    /**
     * Google Web Client ID for Credential Manager.
     * Configured in local.properties as GOOGLE_WEB_CLIENT_ID.
     */
    const val GOOGLE_WEB_CLIENT_ID = BuildConfig.GOOGLE_WEB_CLIENT_ID
}
