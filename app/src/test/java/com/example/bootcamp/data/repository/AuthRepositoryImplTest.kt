package com.example.bootcamp.data.repository

import com.example.bootcamp.data.datasource.AuthLocalDataSource
import com.example.bootcamp.data.datasource.AuthRemoteDataSource
import com.example.bootcamp.data.remote.base.ApiException
import com.example.bootcamp.data.remote.base.ErrorDetails
import com.example.bootcamp.util.ApiResult
import com.example.bootcamp.util.CoroutineTestRule
import com.example.bootcamp.util.TestDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AuthRepositoryImplTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var authRepository: AuthRepositoryImpl
    private lateinit var authRemoteDataSource: AuthRemoteDataSource
    private lateinit var authLocalDataSource: AuthLocalDataSource

    @Before
    fun setup() {
        authRemoteDataSource = mockk()
        authLocalDataSource = mockk(relaxed = true)
        authRepository = AuthRepositoryImpl(authRemoteDataSource, authLocalDataSource)
    }

    // ============== Login Tests ==============

    @Test
    fun `login success saves token and fetches CSRF token`() = runTest {
        // Given
        val loginData = TestDataFactory.createLoginData()
        val csrfData = TestDataFactory.createCsrfTokenData()
        coEvery {
            authRemoteDataSource.login("testuser", "password123", null, null, "ANDROID")
        } returns ApiResult.Success(loginData)
        coEvery {
            authRemoteDataSource.fetchCsrfToken()
        } returns ApiResult.Success(csrfData)
        coEvery { authLocalDataSource.saveXsrfToken(any()) } returns Unit

        // When
        val result = authRepository.login("testuser", "password123", null, null, "ANDROID")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Login successful!", result.getOrNull())
        coVerify {
            authLocalDataSource.saveUserData(
                token = "test_token_123",
                username = "testuser",
                userId = "user_123",
                email = "test@example.com"
            )
        }
        coVerify { authLocalDataSource.saveXsrfToken("masked_csrf_token_123") }
    }

    @Test
    fun `login failure returns error`() = runTest {
        // Given
        coEvery {
            authRemoteDataSource.login(any(), any(), any(), any(), any())
        } returns ApiResult.Error("Invalid credentials", statusCode = 401)

        // When
        val result = authRepository.login("testuser", "wrongpassword", null, null, "ANDROID")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as ApiException
        assertEquals("Invalid credentials", exception.message)
        assertEquals(401, exception.statusCode)
    }

    @Test
    fun `login continues even if CSRF fetch fails`() = runTest {
        // Given
        val loginData = TestDataFactory.createLoginData()
        coEvery {
            authRemoteDataSource.login(any(), any(), any(), any(), any())
        } returns ApiResult.Success(loginData)
        coEvery {
            authRemoteDataSource.fetchCsrfToken()
        } returns ApiResult.Error("CSRF fetch failed")
        coEvery { authLocalDataSource.saveXsrfToken(any()) } returns Unit

        // When
        val result = authRepository.login("testuser", "password123", null, null, "ANDROID")

        // Then - should still succeed
        assertTrue(result.isSuccess)
    }

    // ============== Register Tests ==============

    @Test
    fun `register success returns success message`() = runTest {
        // Given
        val registerData = TestDataFactory.createRegisterData()
        coEvery {
            authRemoteDataSource.register("newuser", "new@example.com", "password123")
        } returns ApiResult.Success(registerData)

        // When
        val result = authRepository.register("newuser", "new@example.com", "password123")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Registration successful!", result.getOrNull())
    }

    @Test
    fun `register failure with field errors returns error`() = runTest {
        // Given
        val errorDetails = ErrorDetails(fieldErrors = mapOf("email" to "Email already exists"))
        coEvery {
            authRemoteDataSource.register(any(), any(), any())
        } returns ApiResult.Error("Validation failed", errorDetails = errorDetails, statusCode = 400)

        // When
        val result = authRepository.register("user", "existing@example.com", "password123")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as ApiException
        assertEquals("Validation failed", exception.message)
        assertEquals("Email already exists", exception.errorDetails?.getFieldError("email"))
    }

    // ============== Google Login Tests ==============

    @Test
    fun `googleLogin success saves token and fetches CSRF token`() = runTest {
        // Given
        val loginData = TestDataFactory.createLoginData()
        val csrfData = TestDataFactory.createCsrfTokenData()
        coEvery {
            authRemoteDataSource.googleLogin("google_id_token", null, null, "ANDROID")
        } returns ApiResult.Success(loginData)
        coEvery {
            authRemoteDataSource.fetchCsrfToken()
        } returns ApiResult.Success(csrfData)
        coEvery { authLocalDataSource.saveXsrfToken(any()) } returns Unit

        // When
        val result = authRepository.googleLogin("google_id_token", null, null, "ANDROID")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Google Login successful!", result.getOrNull())
        coVerify {
            authLocalDataSource.saveUserData(
                token = "test_token_123",
                username = "testuser",
                userId = "user_123",
                email = "test@example.com"
            )
        }
    }

    // ============== Forgot Password Tests ==============

    @Test
    fun `forgotPassword success returns success message`() = runTest {
        // Given
        coEvery {
            authRemoteDataSource.forgotPassword("test@example.com")
        } returns ApiResult.Success(Unit)

        // When
        val result = authRepository.forgotPassword("test@example.com")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Password reset email sent!", result.getOrNull())
    }

    @Test
    fun `forgotPassword failure returns error`() = runTest {
        // Given
        coEvery {
            authRemoteDataSource.forgotPassword(any())
        } returns ApiResult.Error("Email not found", statusCode = 404)

        // When
        val result = authRepository.forgotPassword("nonexistent@example.com")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as ApiException
        assertEquals("Email not found", exception.message)
    }

    // ============== Logout Tests ==============

    @Test
    fun `logout with token calls API and clears token`() = runTest {
        // Given
        coEvery { authLocalDataSource.token } returns flowOf("test_token")
        coEvery { authRemoteDataSource.logout("test_token") } returns ApiResult.Success(Unit)

        // When
        val result = authRepository.logout()

        // Then
        assertTrue(result.isSuccess)
        coVerify { authRemoteDataSource.logout("test_token") }
        coVerify { authLocalDataSource.clearToken() }
    }

    @Test
    fun `logout without token only clears local token`() = runTest {
        // Given
        coEvery { authLocalDataSource.token } returns flowOf(null)

        // When
        val result = authRepository.logout()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { authRemoteDataSource.logout(any()) }
        coVerify { authLocalDataSource.clearToken() }
    }

    @Test
    fun `logout clears token even when API call fails`() = runTest {
        // Given
        coEvery { authLocalDataSource.token } returns flowOf("test_token")
        coEvery { authRemoteDataSource.logout(any()) } returns ApiResult.Error("Network error")

        // When
        val result = authRepository.logout()

        // Then
        assertTrue(result.isSuccess)
        coVerify { authLocalDataSource.clearToken() }
    }

    // ============== Get User Profile Tests ==============

    // ============== Token Flow Tests ==============

    @Test
    fun `getTokenFlow returns token from AuthLocalDataSource`() = runTest {
        // Given
        coEvery { authLocalDataSource.token } returns flowOf("test_token")

        // When
        val token = authRepository.getTokenFlow().first()

        // Then
        assertEquals("test_token", token)
    }

    @Test
    fun `getUsernameFlow returns username from AuthLocalDataSource`() = runTest {
        // Given
        coEvery { authLocalDataSource.username } returns flowOf("testuser")

        // When
        val username = authRepository.getUsernameFlow().first()

        // Then
        assertEquals("testuser", username)
    }
}
