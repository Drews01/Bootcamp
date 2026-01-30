package com.example.bootcamp.di

import android.content.Context
import com.example.bootcamp.data.local.LanguagePreferences
import com.example.bootcamp.util.LanguageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI Module for Language-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {

    @Provides
    @Singleton
    fun provideLanguagePreferences(@ApplicationContext context: Context): LanguagePreferences =
        LanguagePreferences(context)

    @Provides
    @Singleton
    fun provideLanguageManager(
        @ApplicationContext context: Context,
        languagePreferences: LanguagePreferences
    ): LanguageManager = LanguageManager(context, languagePreferences)
}
