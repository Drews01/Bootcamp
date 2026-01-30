package com.example.bootcamp.ui.viewmodel

import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.domain.usecase.auth.ForgotPasswordUseCase
import com.example.bootcamp.domain.usecase.auth.GoogleLoginUseCase
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
    private val loginUseCase: LoginUseCase = mockk()
    private val googleLoginUseCase: GoogleLoginUseCase = mockk()
    private val registerUseCase: RegisterUseCase = mockk()
    private val logoutUseCase: LogoutUseCase = mockk()
    private val forgotPasswordUseCase: ForgotPasswordUseCase = mockk()
    private val authRepository: AuthRepository = mockk()
    private val userProfileRepository: UserProfileRepository = mockk()
    private val loanRepository: LoanRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock repository flows to avoid initialisation errors in init block
        coEvery { authRepository.getTokenFlow() } returns flowOf(null)
        coEvery { authRepository.getUsernameFlow() } returns flowOf(null)
        coEvery { authRepository.getUserIdFlow() } returns flowOf(null)
        coEvery { authRepository.getEmailFlow() } returns flowOf(null)
        coEvery { userProfileRepository.getPendingProfile() } returns flowOf(null)
        coEvery { loanRepository.getPendingLoans() } returns flowOf(emptyList())

        authViewModel = AuthViewModel(
            loginUseCase,
            googleLoginUseCase,
            registerUseCase,
            logoutUseCase,
            forgotPasswordUseCase,
            authRepository,
            userProfileRepository,
            loanRepository
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
        coEvery { loginUseCase(any()) } returns Result.success(successMessage)

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
        coEvery { loginUseCase(any()) } returns Result.failure(Exception(errorMessage))

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
        coEvery { logoutUseCase() } returns Result.success(successMessage)

        // When
        authViewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(successMessage, authViewModel.uiState.value.successMessage)
        assertEquals(false, authViewModel.uiState.value.isLoading)
    }
}
