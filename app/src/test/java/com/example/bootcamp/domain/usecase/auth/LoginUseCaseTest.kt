package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.data.repository.ProductRepository
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.util.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginUseCaseTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var userProfileRepository: UserProfileRepository
    private lateinit var loanRepository: LoanRepository
    private lateinit var productRepository: ProductRepository

    @Before
    fun setup() {
        authRepository = mockk()
        userProfileRepository = mockk(relaxed = true)
        loanRepository = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)

        loginUseCase = LoginUseCase(
            authRepository,
            userProfileRepository,
            loanRepository,
            productRepository
        )
    }

    @Test
    fun `invoke with empty username returns failure`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "",
            password = "password123"
        )

        // When
        val result = loginUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Username or email cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with blank username returns failure`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "   ",
            password = "password123"
        )

        // When
        val result = loginUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Username or email cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty password returns failure`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "testuser",
            password = ""
        )

        // When
        val result = loginUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Password cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with password less than 6 characters returns failure`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "testuser",
            password = "12345"
        )

        // When
        val result = loginUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Password must be at least 6 characters", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with valid credentials clears cache and calls repository`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "testuser",
            password = "password123",
            fcmToken = "fcm_token",
            deviceName = "Test Device"
        )
        coEvery {
            authRepository.login(
                usernameOrEmail = "testuser",
                password = "password123",
                fcmToken = "fcm_token",
                deviceName = "Test Device",
                platform = "ANDROID"
            )
        } returns Result.success("Login successful!")

        // When
        val result = loginUseCase(params)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Login successful!", result.getOrNull())

        // Verify cache clearing
        coVerify { userProfileRepository.clearCache() }
        coVerify { loanRepository.clearCache() }
        coVerify { productRepository.clearCache() }

        // Verify repository call
        coVerify {
            authRepository.login(
                usernameOrEmail = "testuser",
                password = "password123",
                fcmToken = "fcm_token",
                deviceName = "Test Device",
                platform = "ANDROID"
            )
        }
    }

    @Test
    fun `invoke with repository failure returns failure`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "testuser",
            password = "wrongpassword"
        )
        val errorMessage = "Invalid credentials"
        coEvery {
            authRepository.login(
                usernameOrEmail = "testuser",
                password = "wrongpassword",
                fcmToken = null,
                deviceName = null,
                platform = "ANDROID"
            )
        } returns Result.failure(Exception(errorMessage))

        // When
        val result = loginUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke continues even if cache clearing fails`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "testuser",
            password = "password123"
        )
        coEvery { userProfileRepository.clearCache() } throws Exception("Cache clear failed")
        coEvery {
            authRepository.login(any(), any(), any(), any(), any())
        } returns Result.success("Login successful!")

        // When
        val result = loginUseCase(params)

        // Then - should still succeed despite cache clear failure
        assertTrue(result.isSuccess)
        assertEquals("Login successful!", result.getOrNull())
    }

    @Test
    fun `invoke with default platform uses ANDROID`() = runTest {
        // Given
        val params = LoginParams(
            usernameOrEmail = "testuser",
            password = "password123"
        )
        coEvery {
            authRepository.login(any(), any(), any(), any(), "ANDROID")
        } returns Result.success("Login successful!")

        // When
        val result = loginUseCase(params)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            authRepository.login(
                usernameOrEmail = "testuser",
                password = "password123",
                fcmToken = null,
                deviceName = null,
                platform = "ANDROID"
            )
        }
    }
}
