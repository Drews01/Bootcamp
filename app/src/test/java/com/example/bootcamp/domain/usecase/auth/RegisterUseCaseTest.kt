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

class RegisterUseCaseTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = mockk()
        registerUseCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `invoke with empty username returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123"
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Username cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with username less than 3 characters returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "ab",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123"
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Username must be at least 3 characters", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty email returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "testuser",
            email = "",
            password = "password123",
            confirmPassword = "password123"
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Email cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with invalid email format returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "testuser",
            email = "invalid-email",
            password = "password123",
            confirmPassword = "password123"
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid email format", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with invalid email without domain returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "testuser",
            email = "test@",
            password = "password123",
            confirmPassword = "password123"
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid email format", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty password returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "testuser",
            email = "test@example.com",
            password = "",
            confirmPassword = ""
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Password cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with password less than 6 characters returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "testuser",
            email = "test@example.com",
            password = "12345",
            confirmPassword = "12345"
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Password must be at least 6 characters", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with passwords not matching returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password456"
        )

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Passwords do not match", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with valid data calls repository and returns success`() = runTest {
        // Given
        val params = RegisterParams(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123"
        )
        coEvery {
            authRepository.register("testuser", "test@example.com", "password123")
        } returns Result.success("Registration successful!")

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Registration successful!", result.getOrNull())
        coVerify {
            authRepository.register("testuser", "test@example.com", "password123")
        }
    }

    @Test
    fun `invoke with repository failure returns failure`() = runTest {
        // Given
        val params = RegisterParams(
            username = "existinguser",
            email = "existing@example.com",
            password = "password123",
            confirmPassword = "password123"
        )
        val errorMessage = "Email already exists"
        coEvery {
            authRepository.register("existinguser", "existing@example.com", "password123")
        } returns Result.failure(Exception(errorMessage))

        // When
        val result = registerUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with valid email formats accepts various patterns`() = runTest {
        // Given
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.id",
            "test123@test-domain.com"
        )

        coEvery {
            authRepository.register(any(), any(), any())
        } returns Result.success("Registration successful!")

        // When & Then
        validEmails.forEach { email ->
            val params = RegisterParams(
                username = "testuser",
                email = email,
                password = "password123",
                confirmPassword = "password123"
            )
            val result = registerUseCase(params)
            assertTrue("Email $email should be valid", result.isSuccess)
        }
    }
}
