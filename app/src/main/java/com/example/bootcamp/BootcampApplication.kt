package com.example.bootcamp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.bootcamp.util.LanguageManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class annotated with @HiltAndroidApp to enable Hilt dependency injection. This
 * triggers Hilt's code generation and serves as the application-level dependency container.
 *
 * Implements Configuration.Provider to enable HiltWorker for WorkManager.
 */
@HiltAndroidApp
class BootcampApplication :
    Application(),
    Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate() {
        super.onCreate()
        // Apply stored language at app startup
        // This ensures correct locale even before MainActivity
        languageManager.applyStoredLanguage()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
