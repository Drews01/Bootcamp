package com.example.bootcamp.di

import com.example.bootcamp.data.local.TokenManager
import com.example.bootcamp.data.remote.datasource.AuthRemoteDataSource
import com.example.bootcamp.data.repository.AuthRepositoryImpl
import com.example.bootcamp.domain.repository.AuthRepository
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
}
