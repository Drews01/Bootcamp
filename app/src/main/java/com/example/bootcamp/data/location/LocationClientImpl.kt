package com.example.bootcamp.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.example.bootcamp.domain.location.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationClientImpl @Inject constructor(private val client: FusedLocationProviderClient) : LocationClient {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = try {
        client.lastLocation.await()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
