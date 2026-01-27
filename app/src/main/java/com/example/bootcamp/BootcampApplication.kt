package com.example.bootcamp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class annotated with @HiltAndroidApp to enable Hilt dependency injection. This
 * triggers Hilt's code generation and serves as the application-level dependency container.
 * 
 * Implements Configuration.Provider to enable HiltWorker for WorkManager.
 */
@HiltAndroidApp
class BootcampApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        // Application-level initialization can be done here
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

