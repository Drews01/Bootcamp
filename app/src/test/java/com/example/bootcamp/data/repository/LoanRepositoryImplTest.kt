package com.example.bootcamp.data.repository

import com.example.bootcamp.data.datasource.LoanRemoteDataSource
import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.local.dao.BranchDao
import com.example.bootcamp.data.local.dao.LoanHistoryDao
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.entity.BranchEntity
import com.example.bootcamp.data.local.entity.PendingLoanEntity
import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.remote.dto.BranchDropdownItem
import com.example.bootcamp.data.remote.dto.SubmitLoanData
import com.example.bootcamp.data.sync.SyncManager
import com.example.bootcamp.util.ApiResult
import com.example.bootcamp.util.CoroutineTestRule
import com.example.bootcamp.util.NetworkMonitor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoanRepositoryImplTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var loanRepository: LoanRepositoryImpl
    private lateinit var loanRemoteDataSource: LoanRemoteDataSource
    private lateinit var tokenManager: TokenManager
    private lateinit var pendingLoanDao: PendingLoanDao
    private lateinit var branchDao: BranchDao
    private lateinit var loanHistoryDao: LoanHistoryDao
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var syncManager: SyncManager

    @Before
    fun setup() {
        loanRemoteDataSource = mockk()
        tokenManager = mockk()
        pendingLoanDao = mockk(relaxed = true)
        branchDao = mockk(relaxed = true)
        loanHistoryDao = mockk(relaxed = true)
        networkMonitor = mockk()
        syncManager = mockk(relaxed = true)

        loanRepository = LoanRepositoryImpl(
            loanRemoteDataSource,
            tokenManager,
            pendingLoanDao,
            branchDao,
            loanHistoryDao,
            networkMonitor,
            syncManager
        )
    }

    // ============== Submit Loan Tests ==============

    @Test
    fun `submitLoan when online and successful returns success`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns true
        coEvery {
            loanRemoteDataSource.submitLoan(token, 10000000L, 12, 1L, null, null)
        } returns ApiResult.Success(SubmitLoanData(id = 123L, referenceNumber = "REF123", status = "PENDING"))

        // When
        val result = loanRepository.submitLoan(10000000L, 12, 1L, "Jakarta Branch", null, null)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.contains("REF123"))
        coVerify(exactly = 0) { pendingLoanDao.insert(any()) }
    }

    @Test
    fun `submitLoan when offline queues for sync`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns false

        // When
        val result = loanRepository.submitLoan(10000000L, 12, 1L, "Jakarta Branch", null, null)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.contains("queued"))
        coVerify {
            pendingLoanDao.insert(
                match {
                    it.amount == 10000000L &&
                        it.tenureMonths == 12 &&
                        it.branchId == 1L &&
                        it.syncStatus == SyncStatus.PENDING
                }
            )
        }
        verify { syncManager.scheduleLoanSync() }
    }

    @Test
    fun `submitLoan when network error queues for sync`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns true
        coEvery {
            loanRemoteDataSource.submitLoan(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Error("Network error", statusCode = 503)

        // When
        val result = loanRepository.submitLoan(10000000L, 12, 1L, "Jakarta Branch", null, null)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.contains("queued"))
        coVerify { pendingLoanDao.insert(any()) }
    }

    @Test
    fun `submitLoan when business error returns error without queuing`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns true
        coEvery {
            loanRemoteDataSource.submitLoan(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Error("Insufficient credit limit", statusCode = 400)

        // When
        val result = loanRepository.submitLoan(10000000L, 12, 1L, "Jakarta Branch", null, null)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Insufficient credit limit", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { pendingLoanDao.insert(any()) }
    }

    @Test
    fun `submitLoan when not logged in returns failure`() = runTest {
        // Given
        coEvery { tokenManager.token } returns flowOf(null)

        // When
        val result = loanRepository.submitLoan(10000000L, 12, 1L, "Jakarta Branch", null, null)

        // Then
        assertTrue(result.isFailure)
        assertEquals("User not logged in", result.exceptionOrNull()?.message)
    }

    // ============== Get Branches Tests ==============

    @Test
    fun `getBranches when online returns and caches data`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns true
        val remoteBranches = listOf(
            BranchDropdownItem(1L, "Jakarta"),
            BranchDropdownItem(2L, "Bandung")
        )
        coEvery { loanRemoteDataSource.getBranches(token) } returns ApiResult.Success(remoteBranches)

        // When
        val result = loanRepository.getBranches()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Jakarta", result.getOrNull()?.get(0)?.name)
        coVerify {
            branchDao.insertAll(
                match {
                    it.size == 2 && it[0].name == "Jakarta"
                }
            )
        }
    }

    @Test
    fun `getBranches when offline returns cached data`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns false
        val cachedBranches = listOf(
            BranchEntity(1L, "Jakarta"),
            BranchEntity(2L, "Bandung")
        )
        coEvery { branchDao.getAllBranches() } returns cachedBranches

        // When
        val result = loanRepository.getBranches()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Jakarta", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun `getBranches when online fails falls back to cache`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns true
        coEvery { loanRemoteDataSource.getBranches(token) } returns ApiResult.Error("Network error")
        val cachedBranches = listOf(BranchEntity(1L, "Jakarta"))
        coEvery { branchDao.getAllBranches() } returns cachedBranches

        // When
        val result = loanRepository.getBranches()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `getBranches when offline and no cache returns failure`() = runTest {
        // Given
        val token = "test_token"
        coEvery { tokenManager.token } returns flowOf(token)
        every { networkMonitor.isConnected } returns false
        coEvery { branchDao.getAllBranches() } returns emptyList()

        // When
        val result = loanRepository.getBranches()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("No cached branches"))
    }

    // ============== Retry Pending Loan Tests ==============

    @Test
    fun `retryPendingLoan resets retry count and schedules sync`() = runTest {
        // Given
        val pendingLoan = PendingLoanEntity(
            id = 1L,
            amount = 10000000L,
            tenureMonths = 12,
            branchId = 1L,
            branchName = "Jakarta",
            syncStatus = SyncStatus.FAILED,
            retryCount = 3,
            errorMessage = "Previous error"
        )
        coEvery { pendingLoanDao.getById(1L) } returns pendingLoan

        // When
        val result = loanRepository.retryPendingLoan(1L)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            pendingLoanDao.update(
                match {
                    it.syncStatus == SyncStatus.PENDING &&
                        it.retryCount == 0 &&
                        it.errorMessage == null
                }
            )
        }
        verify { syncManager.scheduleLoanSync() }
    }

    @Test
    fun `retryPendingLoan when loan not found returns failure`() = runTest {
        // Given
        coEvery { pendingLoanDao.getById(999L) } returns null

        // When
        val result = loanRepository.retryPendingLoan(999L)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Loan not found", result.exceptionOrNull()?.message)
    }

    // ============== Delete Pending Loan Tests ==============

    @Test
    fun `deletePendingLoan removes loan from database`() = runTest {
        // When
        val result = loanRepository.deletePendingLoan(1L)

        // Then
        assertTrue(result.isSuccess)
        coVerify { pendingLoanDao.deleteById(1L) }
    }

    // ============== Clear Cache Tests ==============

    @Test
    fun `clearCache clears all cached data`() = runTest {
        // When
        loanRepository.clearCache()

        // Then
        coVerify { loanHistoryDao.clearAll() }
        coVerify { branchDao.clearAll() }
        coVerify { pendingLoanDao.clearAll() }
    }
}
