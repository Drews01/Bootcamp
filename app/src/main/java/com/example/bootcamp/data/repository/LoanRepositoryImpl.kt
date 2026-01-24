package com.example.bootcamp.data.repository

import com.example.bootcamp.data.remote.datasource.LoanRemoteDataSource
import com.example.bootcamp.domain.model.Branch
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.util.asResult
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import com.example.bootcamp.data.local.TokenManager

/** Implementation of LoanRepository. Coordinates data sources and maps DTOs to domain models. */
@Singleton
class LoanRepositoryImpl
@Inject
constructor(
    private val loanRemoteDataSource: LoanRemoteDataSource,
    private val tokenManager: TokenManager
) : LoanRepository {

    override suspend fun getBranches(): Result<List<Branch>> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
             return Result.failure(IllegalStateException("User not logged in"))
        }
        return loanRemoteDataSource
                .getBranches(token)
                .map { dtoList -> dtoList.map { dto -> Branch(id = dto.id, name = dto.name) } }
                .asResult()
    }

    override suspend fun submitLoan(
            amount: Long,
            tenureMonths: Int,
            branchId: Long
    ): Result<String> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }
        
        return loanRemoteDataSource
                .submitLoan(token, amount, tenureMonths, branchId)
                .map { data ->
                    "Loan submitted successfully. Reference: ${data.referenceNumber ?: data.id}"
                }
                .asResult()
    }

    override suspend fun getLoanHistory(): Result<List<com.example.bootcamp.domain.model.LoanApplication>> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        return loanRemoteDataSource.getLoanHistory(token)
            .map { dtoList ->
                dtoList.map { dto ->
                    com.example.bootcamp.domain.model.LoanApplication(
                        id = dto.loanApplicationId,
                        productId = dto.productId,
                        productName = dto.productName,
                        amount = dto.amount,
                        tenureMonths = dto.tenureMonths,
                        status = dto.currentStatus,
                        displayStatus = dto.displayStatus,
                        date = dto.createdAt
                    )
                }
            }
            .asResult()
    }

    override suspend fun getUserAvailableCredit(): Result<Double> {
        val token = tokenManager.token.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(IllegalStateException("User not logged in"))
        }

        return loanRemoteDataSource.getUserTier(token)
            .map { tierDto -> tierDto.availableCredit }
            .asResult()
    }
}
