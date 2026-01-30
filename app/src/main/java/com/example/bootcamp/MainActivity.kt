package com.example.bootcamp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bootcamp.ui.navigation.AppNavigation
import com.example.bootcamp.ui.theme.BootcampTheme
import com.example.bootcamp.ui.viewmodel.AuthViewModel
import com.example.bootcamp.util.LanguageManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

/**
 * Main activity for the Bootcamp application. Annotated with @AndroidEntryPoint for Hilt dependency
 * injection.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
    }

    override fun attachBaseContext(newBase: Context) {
        // For API < 33, we need to manually apply locale to context before Activity is created
        // This is critical for Compose's stringResource() to work with the correct locale
        val sharedPrefs = newBase.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
        val languageCode = sharedPrefs.getString("selected_language", "id") ?: "id"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply stored language BEFORE setContent
        languageManager.applyStoredLanguage()

        askNotificationPermission()
        enableEdgeToEdge()

        setContent {
            BootcampTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                AppNavigation(viewModel = authViewModel)
            }
        }
    }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
