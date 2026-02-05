package com.example.bootcamp.domain.usecase.auth

import com.example.bootcamp.domain.repository.AuthRepository
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

class ForgotPasswordUseCaseTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var forgotPasswordUseCase: ForgotPasswordUseCase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = mockk()
        forgotPasswordUseCase = ForgotPasswordUseCase(authRepository)
    }

    @Test
    fun `invoke with empty email returns failure`() = runTest {
        // Given
        val params = ForgotPasswordParams(email = "")

        // When
        val result = forgotPasswordUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Email cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with blank email returns failure`() = runTest {
        // Given
        val params = ForgotPasswordParams(email = "   ")

        // When
        val result = forgotPasswordUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Email cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with invalid email format returns failure`() = runTest {
        // Given
        val params = ForgotPasswordParams(email = "invalid-email")

        // When
        val result = forgotPasswordUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid email format", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with email missing at symbol returns failure`() = runTest {
        // Given
        val params = ForgotPasswordParams(email = "testexample.com")

        // When
        val result = forgotPasswordUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid email format", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with email missing domain returns failure`() = runTest {
        // Given
        val params = ForgotPasswordParams(email = "test@")

        // When
        val result = forgotPasswordUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid email format", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with valid email calls repository and returns success`() = runTest {
        // Given
        val params = ForgotPasswordParams(email = "test@example.com")
        coEvery {
            authRepository.forgotPassword("test@example.com")
        } returns Result.success("Password reset email sent!")

        // When
        val result = forgotPasswordUseCase(params)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Password reset email sent!", result.getOrNull())
        coVerify {
            authRepository.forgotPassword("test@example.com")
        }
    }

    @Test
    fun `invoke with repository failure returns failure`() = runTest {
        // Given
        val params = ForgotPasswordParams(email = "nonexistent@example.com")
        val errorMessage = "Email not found"
        coEvery {
            authRepository.forgotPassword("nonexistent@example.com")
        } returns Result.failure(Exception(errorMessage))

        // When
        val result = forgotPasswordUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with various valid email formats succeeds`() = runTest {
        // Given
        val validEmails = listOf(
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.id",
            "test_123@test-domain.com"
        )

        coEvery {
            authRepository.forgotPassword(any())
        } returns Result.success("Password reset email sent!")

        // When & Then
        validEmails.forEach { email ->
            val params = ForgotPasswordParams(email = email)
            val result = forgotPasswordUseCase(params)
            assertTrue("Email $email should be valid", result.isSuccess)
        }
    }

    @Test
    fun `invoke with various invalid email formats fails`() = runTest {
        // Given
        val invalidEmails = listOf(
            "invalid",
            "@example.com",
            "user@",
            "user @example.com",
            "user@.com"
        )

        // When & Then
        invalidEmails.forEach { email ->
            val params = ForgotPasswordParams(email = email)
            val result = forgotPasswordUseCase(params)
            assertTrue("Email $email should be invalid", result.isFailure)
            assertEquals("Invalid email format", result.exceptionOrNull()?.message)
        }
    }
}
