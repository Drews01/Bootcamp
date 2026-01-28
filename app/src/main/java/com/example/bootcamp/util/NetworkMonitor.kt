package com.example.bootcamp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors network connectivity status. Used for offline-first logic to determine when to fetch
 * from network vs cache.
 */
@Singleton
class NetworkMonitor @Inject constructor(@ApplicationContext private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /** Returns true if the device is currently connected to the internet. */
    val isConnected: Boolean
        get() {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

    /** Returns a Flow that emits connectivity status changes. */
    val connectivityFlow: Flow<Boolean> =
        callbackFlow {
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        trySend(true)
                    }

                    override fun onLost(network: Network) {
                        trySend(false)
                    }

                    override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                        val isConnected =
                            capabilities.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_INTERNET
                            ) &&
                                capabilities.hasCapability(
                                    NetworkCapabilities
                                        .NET_CAPABILITY_VALIDATED
                                )
                        trySend(isConnected)
                    }
                }

            val request =
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            connectivityManager.registerNetworkCallback(request, callback)

            // Emit initial state
            trySend(isConnected)

            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        }
            .distinctUntilChanged()
}
