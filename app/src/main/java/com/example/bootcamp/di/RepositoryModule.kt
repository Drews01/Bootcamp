package com.example.bootcamp.di

import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.remote.datasource.AuthRemoteDataSource
import com.example.bootcamp.data.remote.datasource.LoanRemoteDataSource
import com.example.bootcamp.data.repository.AuthRepositoryImpl
import com.example.bootcamp.data.repository.LoanRepositoryImpl
import com.example.bootcamp.domain.repository.AuthRepository
import com.example.bootcamp.domain.repository.LoanRepository
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

    /** Provides LoanRepository implementation. */
    @Provides
    @Singleton
    fun provideLoanRepository(
            loanRemoteDataSource: LoanRemoteDataSource,
            tokenManager: TokenManager
    ): LoanRepository {
        return LoanRepositoryImpl(loanRemoteDataSource, tokenManager)
    }

    /** Provides UserProfileRepository implementation. */
    @Provides
    @Singleton
    fun provideUserProfileRepository(
            userProfileRemoteDataSource:
                    com.example.bootcamp.data.remote.datasource.UserProfileRemoteDataSource,
            tokenManager: TokenManager
    ): com.example.bootcamp.domain.repository.UserProfileRepository {
        return com.example.bootcamp.data.repository.UserProfileRepositoryImpl(
                userProfileRemoteDataSource,
                tokenManager
        )
    }
}
