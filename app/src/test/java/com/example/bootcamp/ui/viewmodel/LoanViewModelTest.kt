package com.example.bootcamp.ui.viewmodel

import com.example.bootcamp.domain.location.LocationClient
import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.usecase.loan.GetBranchesUseCase
import com.example.bootcamp.domain.usecase.loan.SubmitLoanUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoanViewModelTest {

    private lateinit var loanViewModel: LoanViewModel
    private val submitLoanUseCase: SubmitLoanUseCase = mockk()
    private val loanRepository: LoanRepository = mockk()
    private val locationClient: LocationClient = mockk()

    private val testDispatcher = StandardTestDispatcher()

    class FakeGetBranchesUseCase(repo: LoanRepository) : GetBranchesUseCase(repo) {
        override suspend fun invoke(): Result<List<Branch>> = Result.success(emptyList())
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val getBranchesUseCase = FakeGetBranchesUseCase(loanRepository)
        val savedStateHandle = androidx.lifecycle.SavedStateHandle()

        loanViewModel = LoanViewModel(
            savedStateHandle,
            getBranchesUseCase,
            submitLoanUseCase,
            loanRepository,
            locationClient
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAmountChanged formats valid amount with commas`() = runTest {
        // Given
        val input = "1000000"

        // When
        loanViewModel.onAmountChanged(input)

        // Then
        assertEquals("1,000,000", loanViewModel.uiState.value.amount)
    }

    @Test
    fun `onAmountChanged ignores amounts greater than 50 million`() = runTest {
        // Given initial state with 50,000,000
        loanViewModel.onAmountChanged("50000000")
        assertEquals("50,000,000", loanViewModel.uiState.value.amount)

        // When trying to input 50,000,001
        loanViewModel.onAmountChanged("50000001")

        // Then amount should remain 50,000,000 (ignored)
        assertEquals("50,000,000", loanViewModel.uiState.value.amount)
    }

    @Test
    fun `onAmountChanged allows exactly 50 million`() = runTest {
        // When
        loanViewModel.onAmountChanged("50000000")

        // Then
        assertEquals("50,000,000", loanViewModel.uiState.value.amount)
    }

    @Test
    fun `onAmountChanged handles empty input`() = runTest {
        // Given
        loanViewModel.onAmountChanged("123")

        // When
        loanViewModel.onAmountChanged("")

        // Then
        assertEquals("", loanViewModel.uiState.value.amount)
    }
}
