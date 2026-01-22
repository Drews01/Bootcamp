package com.example.bootcamp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp to enable Hilt dependency injection. This
 * triggers Hilt's code generation and serves as the application-level dependency container.
 */
@HiltAndroidApp
class BootcampApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application-level initialization can be done here
    }
}
