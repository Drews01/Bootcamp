package com.example.bootcamp.di

import com.example.bootcamp.data.datasource.AuthRemoteDataSource
import com.example.bootcamp.data.datasource.LoanRemoteDataSource
import com.example.bootcamp.data.datasource.UserProfileRemoteDataSource
import com.example.bootcamp.data.remote.datasource.AuthRemoteDataSourceImpl
import com.example.bootcamp.data.remote.datasource.LoanRemoteDataSourceImpl
import com.example.bootcamp.data.remote.datasource.UserProfileRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing data source bindings.
 * Binds data source interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    /** Binds AuthRemoteDataSource interface to its implementation. */
    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    /** Binds LoanRemoteDataSource interface to its implementation. */
    @Binds
    @Singleton
    abstract fun bindLoanRemoteDataSource(impl: LoanRemoteDataSourceImpl): LoanRemoteDataSource

    /** Binds UserProfileRemoteDataSource interface to its implementation. */
    @Binds
    @Singleton
    abstract fun bindUserProfileRemoteDataSource(impl: UserProfileRemoteDataSourceImpl): UserProfileRemoteDataSource
}
