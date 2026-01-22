package com.example.bootcamp.di

import android.content.Context
import androidx.room.Room
import com.example.bootcamp.data.local.dao.UserDao
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
}
