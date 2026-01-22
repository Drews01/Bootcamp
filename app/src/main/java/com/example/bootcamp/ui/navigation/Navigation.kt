package com.example.bootcamp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bootcamp.ui.screens.ForgotPasswordScreen
import com.example.bootcamp.ui.screens.HomeScreen
import com.example.bootcamp.ui.screens.LoginScreen
import com.example.bootcamp.ui.screens.RegisterScreen
import com.example.bootcamp.ui.viewmodel.AuthViewModel

/** Navigation routes for the app. */
object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
}

/** Main navigation composable setting up the NavHost. */
@Composable
fun AppNavigation(
        viewModel: AuthViewModel,
        navController: NavHostController = rememberNavController(),
) {
    NavHost(
            navController = navController,
            startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                    viewModel = viewModel,
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    },
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { navController.popBackStack() },
            )
        }
    }
}
