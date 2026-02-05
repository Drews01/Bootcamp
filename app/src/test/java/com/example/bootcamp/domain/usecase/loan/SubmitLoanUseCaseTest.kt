package com.example.bootcamp.domain.usecase.loan

import com.example.bootcamp.domain.repository.LoanRepository
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

class SubmitLoanUseCaseTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var submitLoanUseCase: SubmitLoanUseCase
    private lateinit var loanRepository: LoanRepository

    @Before
    fun setup() {
        loanRepository = mockk()
        submitLoanUseCase = SubmitLoanUseCase(loanRepository)
    }

    @Test
    fun `invoke with zero amount returns failure`() = runTest {
        // Given
        val params = SubmitLoanParams(
            amount = 0L,
            tenureMonths = 12,
            branchId = 1L,
            branchName = "Jakarta Branch"
        )

        // When
        val result = submitLoanUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Amount must be positive", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with negative amount returns failure`() = runTest {
        // Given
        val params = SubmitLoanParams(
            amount = -10000L,
            tenureMonths = 12,
            branchId = 1L,
            branchName = "Jakarta Branch"
        )

        // When
        val result = submitLoanUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Amount must be positive", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with zero tenure returns failure`() = runTest {
        // Given
        val params = SubmitLoanParams(
            amount = 10000000L,
            tenureMonths = 0,
            branchId = 1L,
            branchName = "Jakarta Branch"
        )

        // When
        val result = submitLoanUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Tenure must be positive", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with negative tenure returns failure`() = runTest {
        // Given
        val params = SubmitLoanParams(
            amount = 10000000L,
            tenureMonths = -12,
            branchId = 1L,
            branchName = "Jakarta Branch"
        )

        // When
        val result = submitLoanUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Tenure must be positive", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with valid params calls repository and returns success`() = runTest {
        // Given
        val params = SubmitLoanParams(
            amount = 10000000L,
            tenureMonths = 12,
            branchId = 1L,
            branchName = "Jakarta Branch",
            latitude = -6.2088,
            longitude = 106.8456
        )
        coEvery {
            loanRepository.submitLoan(
                amount = 10000000L,
                tenureMonths = 12,
                branchId = 1L,
                branchName = "Jakarta Branch",
                latitude = -6.2088,
                longitude = 106.8456
            )
        } returns Result.success("Loan submitted successfully")

        // When
        val result = submitLoanUseCase(params)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Loan submitted successfully", result.getOrNull())
        coVerify {
            loanRepository.submitLoan(
                amount = 10000000L,
                tenureMonths = 12,
                branchId = 1L,
                branchName = "Jakarta Branch",
                latitude = -6.2088,
                longitude = 106.8456
            )
        }
    }

    @Test
    fun `invoke without location data succeeds`() = runTest {
        // Given
        val params = SubmitLoanParams(
            amount = 10000000L,
            tenureMonths = 12,
            branchId = 1L,
            branchName = "Jakarta Branch",
            latitude = null,
            longitude = null
        )
        coEvery {
            loanRepository.submitLoan(
                amount = 10000000L,
                tenureMonths = 12,
                branchId = 1L,
                branchName = "Jakarta Branch",
                latitude = null,
                longitude = null
            )
        } returns Result.success("Loan submitted successfully")

        // When
        val result = submitLoanUseCase(params)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke with repository failure returns failure`() = runTest {
        // Given
        val params = SubmitLoanParams(
            amount = 10000000L,
            tenureMonths = 12,
            branchId = 1L,
            branchName = "Jakarta Branch"
        )
        val errorMessage = "Insufficient credit limit"
        coEvery {
            loanRepository.submitLoan(any(), any(), any(), any(), any(), any())
        } returns Result.failure(Exception(errorMessage))

        // When
        val result = submitLoanUseCase(params)

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with various valid amounts succeeds`() = runTest {
        // Given
        val validAmounts = listOf(1L, 1000000L, 50000000L, 100000000L)
        coEvery {
            loanRepository.submitLoan(any(), any(), any(), any(), any(), any())
        } returns Result.success("Loan submitted successfully")

        // When & Then
        validAmounts.forEach { amount ->
            val params = SubmitLoanParams(
                amount = amount,
                tenureMonths = 12,
                branchId = 1L,
                branchName = "Jakarta Branch"
            )
            val result = submitLoanUseCase(params)
            assertTrue("Amount $amount should be valid", result.isSuccess)
        }
    }

    @Test
    fun `invoke with various valid tenures succeeds`() = runTest {
        // Given
        val validTenures = listOf(1, 6, 12, 24, 36)
        coEvery {
            loanRepository.submitLoan(any(), any(), any(), any(), any(), any())
        } returns Result.success("Loan submitted successfully")

        // When & Then
        validTenures.forEach { tenure ->
            val params = SubmitLoanParams(
                amount = 10000000L,
                tenureMonths = tenure,
                branchId = 1L,
                branchName = "Jakarta Branch"
            )
            val result = submitLoanUseCase(params)
            assertTrue("Tenure $tenure should be valid", result.isSuccess)
        }
    }
}
