package com.example.bootcamp.domain.location

import android.location.Location

interface LocationClient {
    suspend fun getCurrentLocation(): Location?
}
