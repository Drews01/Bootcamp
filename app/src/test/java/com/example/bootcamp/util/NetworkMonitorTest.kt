package com.example.bootcamp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NetworkMonitorTest {

    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var network: Network
    private lateinit var networkCapabilities: NetworkCapabilities

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        connectivityManager = mockk()
        network = mockk()
        networkCapabilities = mockk()

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        networkMonitor = NetworkMonitor(context)
    }

    @Test
    fun `isConnected returns true when network is available and validated`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true

        // When
        val isConnected = networkMonitor.isConnected

        // Then
        assertTrue(isConnected)
    }

    @Test
    fun `isConnected returns false when no active network`() {
        // Given
        every { connectivityManager.activeNetwork } returns null

        // When
        val isConnected = networkMonitor.isConnected

        // Then
        assertFalse(isConnected)
    }

    @Test
    fun `isConnected returns false when network capabilities are null`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null

        // When
        val isConnected = networkMonitor.isConnected

        // Then
        assertFalse(isConnected)
    }

    @Test
    fun `isConnected returns false when network has no internet capability`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true

        // When
        val isConnected = networkMonitor.isConnected

        // Then
        assertFalse(isConnected)
    }

    @Test
    fun `isConnected returns false when network is not validated`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns false

        // When
        val isConnected = networkMonitor.isConnected

        // Then
        assertFalse(isConnected)
    }
}
