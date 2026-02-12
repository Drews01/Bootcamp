package com.example.bootcamp.ui.viewmodel

import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.domain.service.FCMService
import com.example.bootcamp.domain.usecase.auth.ForgotPasswordUseCase
import com.example.bootcamp.domain.usecase.auth.GoogleLoginUseCase
import com.example.bootcamp.domain.usecase.auth.LoginParams
import com.example.bootcamp.domain.usecase.auth.LoginUseCase
import com.example.bootcamp.domain.usecase.auth.LogoutUseCase
import com.example.bootcamp.domain.usecase.auth.RegisterUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var authViewModel: AuthViewModel

    // Fakes
    private lateinit var loginUseCase: FakeLoginUseCase
    private lateinit var logoutUseCase: FakeLogoutUseCase

    private val googleLoginUseCase: GoogleLoginUseCase = mockk()
    private val registerUseCase: RegisterUseCase = mockk()
    private val forgotPasswordUseCase: ForgotPasswordUseCase = mockk()
    private val sessionRepository: com.example.bootcamp.domain.repository.SessionRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val userProfileRepository: UserProfileRepository = mockk()
    private val loanRepository: LoanRepository = mockk()
    private val fcmService: FCMService = mockk()

    private val testDispatcher = StandardTestDispatcher()

    // Fake implementations to assume Result handling works correctly (avoiding MockK inline class issues)
    private inner class FakeLoginUseCase :
        LoginUseCase(authRepository, userProfileRepository, loanRepository, mockk()) {
        var activeResult: Result<String> = Result.success("Success")
        override suspend fun invoke(params: LoginParams): Result<String> = activeResult
    }

    private inner class FakeLogoutUseCase :
        LogoutUseCase(authRepository, userProfileRepository, loanRepository, mockk(), mockk(), fcmService) {
        var activeResult: Result<String> = Result.success("Success")
        override suspend fun invoke(): Result<String> = activeResult
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock repository flows to avoid initialisation errors in init block
        coEvery { sessionRepository.getTokenFlow() } returns flowOf(null)
        coEvery { sessionRepository.getUsernameFlow() } returns flowOf(null)
        coEvery { sessionRepository.getUserIdFlow() } returns flowOf(null)
        coEvery { sessionRepository.getEmailFlow() } returns flowOf(null)
        coEvery { userProfileRepository.observePendingProfile() } returns flowOf(null)
        coEvery { loanRepository.observePendingLoans() } returns flowOf(emptyList())
        coEvery { fcmService.getToken() } returns "dummy_token"

        loginUseCase = FakeLoginUseCase()
        logoutUseCase = FakeLogoutUseCase()

        authViewModel = AuthViewModel(
            loginUseCase,
            googleLoginUseCase,
            registerUseCase,
            logoutUseCase,
            forgotPasswordUseCase,
            authRepository,
            sessionRepository,
            userProfileRepository,
            loanRepository,
            fcmService
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates state to success`() = runTest {
        // Given
        val successMessage = "Login successful"
        loginUseCase.activeResult = Result.success(successMessage)

        // When
        authViewModel.login("testuser", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(successMessage, authViewModel.uiState.value.successMessage)
        assertEquals(false, authViewModel.uiState.value.isLoading)
    }

    @Test
    fun `login failure updates state to error`() = runTest {
        // Given
        val errorMessage = "Invalid credentials"
        loginUseCase.activeResult = Result.failure(Exception(errorMessage))

        // When
        authViewModel.login("testuser", "wrongpassword")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(errorMessage, authViewModel.uiState.value.errorMessage)
        assertEquals(false, authViewModel.uiState.value.isLoading)
    }

    @Test
    fun `logout success updates state`() = runTest {
        // Given
        val successMessage = "Logged out successfully"
        logoutUseCase.activeResult = Result.success(successMessage)

        // When
        authViewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(successMessage, authViewModel.uiState.value.successMessage)
        assertEquals(false, authViewModel.uiState.value.isLoading)
    }
}
