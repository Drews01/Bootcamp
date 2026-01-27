package com.example.bootcamp.di

import android.content.Context
import androidx.room.Room
import com.example.bootcamp.data.local.dao.BranchDao
import com.example.bootcamp.data.local.dao.LoanHistoryDao
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.dao.PendingProfileDao
import com.example.bootcamp.data.local.dao.UserDao
import com.example.bootcamp.data.local.dao.UserProfileCacheDao
import com.example.bootcamp.data.local.dao.UserTierDao
import com.example.bootcamp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt module providing database-related dependencies. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /** Provides the Room database instance. */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    /** Provides the UserDao from the database. */
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    /** Provides the PendingLoanDao from the database. */
    @Provides
    @Singleton
    fun providePendingLoanDao(database: AppDatabase): PendingLoanDao {
        return database.pendingLoanDao()
    }

    /** Provides the PendingProfileDao from the database. */
    @Provides
    @Singleton
    fun providePendingProfileDao(database: AppDatabase): PendingProfileDao {
        return database.pendingProfileDao()
    }

    /** Provides the BranchDao from the database. */
    @Provides
    @Singleton
    fun provideBranchDao(database: AppDatabase): BranchDao {
        return database.branchDao()
    }

    /** Provides the UserTierDao for caching tier info. */
    @Provides
    @Singleton
    fun provideUserTierDao(database: AppDatabase): UserTierDao {
        return database.userTierDao()
    }

    /** Provides the LoanHistoryDao for caching loan history. */
    @Provides
    @Singleton
    fun provideLoanHistoryDao(database: AppDatabase): LoanHistoryDao {
        return database.loanHistoryDao()
    }

    /** Provides the UserProfileCacheDao for caching user profile. */
    @Provides
    @Singleton
    fun provideUserProfileCacheDao(database: AppDatabase): UserProfileCacheDao {
        return database.userProfileCacheDao()
    }
}


