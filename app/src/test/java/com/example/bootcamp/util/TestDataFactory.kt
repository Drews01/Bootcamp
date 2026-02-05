package com.example.bootcamp.util

import com.example.bootcamp.data.local.entity.SyncStatus
import com.example.bootcamp.data.remote.dto.CsrfTokenData
import com.example.bootcamp.data.remote.dto.LoginData
import com.example.bootcamp.data.remote.dto.RegisterData
import com.example.bootcamp.data.remote.dto.UserData
import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.model.LoanApplication
import com.example.bootcamp.domain.model.LoanMilestone
import com.example.bootcamp.domain.model.MilestoneStatus
import com.example.bootcamp.domain.model.PendingLoan
import com.example.bootcamp.domain.model.UserProfile

/**
 * Factory for creating test data objects.
 * Provides consistent test data across all tests.
 */
object TestDataFactory {

    // ============== Auth DTOs ==============

    fun createLoginData(
        token: String = "test_token_123",
        userId: String = "user_123",
        username: String = "testuser",
        email: String = "test@example.com",
        roles: List<String> = listOf("USER")
    ) = LoginData(
        token = token,
        userId = userId,
        username = username,
        email = email,
        roles = roles
    )

    fun createRegisterData(
        userId: String = "user_123",
        username: String = "testuser",
        email: String = "test@example.com",
        message: String = "Registration successful!"
    ) = RegisterData(
        userId = userId,
        username = username,
        email = email,
        message = message
    )

    fun createUserData(
        id: String = "user_123",
        username: String = "testuser",
        email: String = "test@example.com",
        fullName: String? = "Test User",
        phoneNumber: String? = "081234567890"
    ) = UserData(
        id = id,
        username = username,
        email = email,
        fullName = fullName,
        phoneNumber = phoneNumber
    )

    fun createCsrfTokenData(
        token: String = "masked_csrf_token_123",
        headerName: String = "X-XSRF-TOKEN",
        parameterName: String = "_csrf"
    ) = CsrfTokenData(
        token = token,
        headerName = headerName,
        parameterName = parameterName
    )

    // ============== Domain Models ==============

    fun createUserProfile(
        username: String = "testuser",
        email: String = "test@example.com",
        address: String? = "123 Test Street",
        nik: String? = "1234567890123456",
        ktpPath: String? = null,
        phoneNumber: String? = "081234567890",
        accountNumber: String? = "1234567890",
        bankName: String? = "BCA",
        updatedAt: String? = "2026-02-05T15:00:00Z"
    ) = UserProfile(
        username = username,
        email = email,
        address = address,
        nik = nik,
        ktpPath = ktpPath,
        phoneNumber = phoneNumber,
        accountNumber = accountNumber,
        bankName = bankName,
        updatedAt = updatedAt
    )

    fun createBranch(id: Long = 1L, name: String = "Jakarta Branch") = Branch(
        id = id,
        name = name
    )

    fun createLoanApplication(
        id: Long = 1L,
        productId: Long = 1L,
        productName: String = "Personal Loan",
        amount: Double = 10000000.0,
        tenureMonths: Int = 12,
        status: String = "PENDING",
        displayStatus: String = "Pending Review",
        date: String = "2026-02-05T15:00:00Z"
    ) = LoanApplication(
        id = id,
        productId = productId,
        productName = productName,
        amount = amount,
        tenureMonths = tenureMonths,
        status = status,
        displayStatus = displayStatus,
        date = date
    )

    fun createLoanMilestone(
        name: String = "Submitted",
        status: MilestoneStatus = MilestoneStatus.COMPLETED,
        timestamp: String? = "2026-02-05T15:00:00Z",
        order: Int = 1
    ) = LoanMilestone(
        name = name,
        status = status,
        timestamp = timestamp,
        order = order
    )

    fun createPendingLoan(
        id: Long = 1L,
        amount: Long = 10000000L,
        tenureMonths: Int = 12,
        branchId: Long = 1L,
        branchName: String = "Jakarta Branch",
        syncStatus: SyncStatus = SyncStatus.PENDING,
        errorMessage: String? = null,
        retryCount: Int = 0,
        createdAt: Long = System.currentTimeMillis()
    ) = PendingLoan(
        id = id,
        amount = amount,
        tenureMonths = tenureMonths,
        branchId = branchId,
        branchName = branchName,
        syncStatus = syncStatus,
        errorMessage = errorMessage,
        retryCount = retryCount,
        createdAt = createdAt
    )

    // ============== Helper Functions ==============

    fun createBranchList(count: Int = 3): List<Branch> = (1..count).map {
        createBranch(id = it.toLong(), name = "Branch $it")
    }

    fun createLoanApplicationList(count: Int = 5): List<LoanApplication> = (1..count).map {
        createLoanApplication(
            id = it.toLong(),
            amount = 10000000.0 * it,
            tenureMonths = 12 * it
        )
    }

    fun createLoanMilestoneList(): List<LoanMilestone> = listOf(
        createLoanMilestone(name = "Submitted", status = MilestoneStatus.COMPLETED, order = 1),
        createLoanMilestone(name = "Marketing Review", status = MilestoneStatus.COMPLETED, order = 2),
        createLoanMilestone(name = "Branch Manager Approval", status = MilestoneStatus.CURRENT, order = 3),
        createLoanMilestone(name = "Back Office Processing", status = MilestoneStatus.PENDING, order = 4),
        createLoanMilestone(name = "Disbursed", status = MilestoneStatus.PENDING, order = 5)
    )
}
