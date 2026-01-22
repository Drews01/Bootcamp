package com.example.bootcamp.domain.model

/** Domain model representing a User. This is a clean domain entity independent of data sources. */
data class User(
        val id: String = "",
        val username: String = "",
        val email: String = "",
        val token: String? = null,
        val isLoggedIn: Boolean = false
)
