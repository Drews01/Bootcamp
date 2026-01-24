package com.example.bootcamp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bootcamp.ui.screens.ForgotPasswordScreen
import com.example.bootcamp.ui.screens.HomeScreen
import com.example.bootcamp.ui.screens.LoginScreen
import com.example.bootcamp.ui.screens.RegisterScreen
import com.example.bootcamp.ui.screens.SubmitLoanScreen
import com.example.bootcamp.ui.screens.UserProfileScreen
import com.example.bootcamp.ui.viewmodel.AuthViewModel

/** Navigation routes for the app. */
object Routes {
    const val HOME = "home"
    const val SUBMIT_LOAN = "submit_loan"
    const val USER_PROFILE = "user_profile"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PROFILE_DETAILS = "profile_details"
    const val LOAN_HISTORY = "loan_history"
}

/** Main navigation composable setting up the NavHost. */
@Composable
fun AppNavigation(
        viewModel: AuthViewModel,
        navController: NavHostController = rememberNavController(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in setOf(Routes.HOME, Routes.SUBMIT_LOAN, Routes.USER_PROFILE)

    Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(
                            currentRoute = currentRoute,
                            isLoggedIn = uiState.isLoggedIn,
                            onNavigate = { target ->
                                if (currentRoute != target) {
                                    navController.navigate(target) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                    )
                }
            },
    ) { innerPadding ->
        AppNavHost(
                viewModel = viewModel,
                navController = navController,
                paddingValues = innerPadding,
        )
    }
}

@Composable
private fun AppNavHost(
        viewModel: AuthViewModel,
        navController: NavHostController,
        paddingValues: PaddingValues,
) {
    NavHost(
            navController = navController,
            startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                    viewModel = viewModel,
                    modifier = androidx.compose.ui.Modifier.padding(paddingValues),
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                    onNavigateToSubmitLoan = { navController.navigate(Routes.SUBMIT_LOAN) },
            )
        }

        composable(Routes.SUBMIT_LOAN) {
            val loanViewModel: com.example.bootcamp.ui.viewmodel.LoanViewModel =
                    androidx.hilt.navigation.compose.hiltViewModel()
            SubmitLoanScreen(
                    viewModel = loanViewModel,
                    modifier = androidx.compose.ui.Modifier.padding(paddingValues),
                    onSubmitSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
            )
        }

        composable(Routes.USER_PROFILE) {
            UserProfileScreen(
                    viewModel = viewModel,
                    modifier = androidx.compose.ui.Modifier.padding(paddingValues),
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                    onNavigateToProfileDetails = { navController.navigate(Routes.PROFILE_DETAILS) },
                    onNavigateToLoanHistory = { navController.navigate(Routes.LOAN_HISTORY) }
            )
        }

        composable(Routes.PROFILE_DETAILS) {
            com.example.bootcamp.ui.screens.ProfileDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LOAN_HISTORY) {
            com.example.bootcamp.ui.screens.LoanHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
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

@Composable
private fun BottomNavigationBar(
        currentRoute: String?,
        isLoggedIn: Boolean,
        onNavigate: (String) -> Unit,
) {
    val destinations =
            if (isLoggedIn) {
                listOf(Routes.HOME, Routes.SUBMIT_LOAN, Routes.USER_PROFILE)
            } else {
                listOf(Routes.HOME, Routes.USER_PROFILE)
            }

    NavigationBar {
        destinations.forEach { route ->
            val selected = currentRoute == route
            val (label, icon) =
                    when (route) {
                        Routes.HOME -> "Home" to Icons.Filled.Home
                        Routes.SUBMIT_LOAN -> "Ajukan" to Icons.Filled.Add
                        Routes.USER_PROFILE -> "Profile" to Icons.Filled.AccountCircle
                        else -> route to Icons.Filled.Home
                    }

            NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(route) },
                    icon = { Icon(imageVector = icon, contentDescription = label) },
                    label = { Text(text = label) },
            )
        }
    }
}
