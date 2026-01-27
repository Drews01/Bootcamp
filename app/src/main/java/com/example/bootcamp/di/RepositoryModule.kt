package com.example.bootcamp.di

import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.local.dao.BranchDao
import com.example.bootcamp.data.local.dao.LoanHistoryDao
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.dao.PendingProfileDao
import com.example.bootcamp.data.local.dao.UserProfileCacheDao
import com.example.bootcamp.data.remote.datasource.AuthRemoteDataSource
import com.example.bootcamp.data.remote.datasource.LoanRemoteDataSource
import com.example.bootcamp.data.remote.datasource.UserProfileRemoteDataSource
import com.example.bootcamp.data.repository.AuthRepositoryImpl
import com.example.bootcamp.data.repository.LoanRepositoryImpl
import com.example.bootcamp.data.repository.UserProfileRepositoryImpl
import com.example.bootcamp.data.sync.SyncManager
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
import com.example.bootcamp.domain.repository.UserProfileRepository
import com.example.bootcamp.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository dependencies. Binds repository implementations to their
 * interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides AuthRepository implementation. Using interface binding for testability and SOLID
     * principles.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(authRemoteDataSource, tokenManager)
    }

    /** Provides LoanRepository implementation with offline-first support. */
    @Provides
    @Singleton
    fun provideLoanRepository(
        loanRemoteDataSource: LoanRemoteDataSource,
        tokenManager: TokenManager,
        pendingLoanDao: PendingLoanDao,
        branchDao: BranchDao,
        loanHistoryDao: LoanHistoryDao,
        networkMonitor: NetworkMonitor,
        syncManager: SyncManager
    ): LoanRepository {
        return LoanRepositoryImpl(
            loanRemoteDataSource,
            tokenManager,
            pendingLoanDao,
            branchDao,
            loanHistoryDao,
            networkMonitor,
            syncManager
        )
    }

    /** Provides UserProfileRepository implementation with offline-first support. */
    @Provides
    @Singleton
    fun provideUserProfileRepository(
        userProfileRemoteDataSource: UserProfileRemoteDataSource,
        tokenManager: TokenManager,
        pendingProfileDao: PendingProfileDao,
        userProfileCacheDao: UserProfileCacheDao,
        networkMonitor: NetworkMonitor,
        syncManager: SyncManager
    ): UserProfileRepository {
        return UserProfileRepositoryImpl(
            userProfileRemoteDataSource,
            tokenManager,
            pendingProfileDao,
            userProfileCacheDao,
            networkMonitor,
            syncManager
        )
    }
}


