package com.example.bootcamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bootcamp.ui.navigation.AppNavigation
import com.example.bootcamp.ui.theme.BootcampTheme
import com.example.bootcamp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Bootcamp application. Annotated with @AndroidEntryPoint for Hilt dependency
 * injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BootcampTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                AppNavigation(viewModel = authViewModel)
            }
        }
    }
}
