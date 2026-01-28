package com.example.bootcamp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.bootcamp.data.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Extension property to create DataStore instance. */
private val Context.dataStore: DataStore<Preferences> by
    preferencesDataStore(name = "app_preferences")

/**
 * Hilt module providing application-wide dependencies. Installed in SingletonComponent to ensure
 * single instances across the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Provides the DataStore instance for preferences storage. */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    /** Provides the TokenManager for handling authentication tokens. */
    @Provides
    @Singleton
    fun provideTokenManager(dataStore: DataStore<Preferences>): TokenManager = TokenManager(dataStore)
}
