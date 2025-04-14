package com.example.autoclickerapp.navigation.app_navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.autoclickerapp.BuildConfig
import com.example.autoclickerapp.R
import com.example.autoclickerapp.navigation.screen.Screen
import com.example.autoclickerapp.navigation.bottom_navbar.BottomNavigationBar
import com.example.autoclickerapp.navigation.bottom_navbar.navbar_items.BottomNavItem
import com.example.autoclickerapp.view.user_view.LoginScreen
import com.example.autoclickerapp.view.on_boarding.OnBoardingScreen
import com.example.autoclickerapp.view.user_view.SignUpScreen
import com.example.autoclickerapp.view.user_view.UserProfileScreen
import com.example.autoclickerapp.view.user_view.HomeScreen
import com.example.autoclickerapp.viewmodel.AuthViewModel


@Composable
fun AppNavigation(
    startDestination: String,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    println("Current route: $currentRoute")

    val bottomNavItems = listOf(
        BottomNavItem("Home", R.drawable.home_ic),
        BottomNavItem("Profile", R.drawable.person_ic),
    )

    val selectedItemIndex = when {
        currentRoute?.contains(Screen.Home.route) == true -> 0
        currentRoute?.contains(Screen.UserProfile.route) == true -> 1
        else -> -1 // No selection for other screens
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom navigation only on Home and UserProfile screens
            if (currentRoute?.contains(Screen.Home.route) == true || currentRoute?.contains(Screen.UserProfile.route) == true) {
                BottomNavigationBar(
                    items = bottomNavItems,
                    selectedItem = selectedItemIndex,
                    onItemSelected = { index ->
                        val route = when (index) {
                            0 -> Screen.Home.route
                            1 -> Screen.UserProfile.route
                            else -> Screen.Home.route
                        }
                        // Navigate to the selected route
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Login Screen
            composable("login_screen/{role}") { backStackEntry ->
                // Get the role parameter from the backStackEntry arguments
                val role = backStackEntry.arguments?.getString("role")?.toIntOrNull() ?: 2

                // Navigate to LoginScreen, passing the role
                LoginScreen(
                    navController = navController,
                    role = role,
                    onNavigateToOnBoarding = {
                        navController.navigate(Screen.OnBoarding.route)
                    },
                    viewModel = authViewModel

                )
            }

            // Home Screen
            composable("home_screen/{role}") { backStackEntry ->
                val role = backStackEntry.arguments?.getString("role")?.toIntOrNull() ?: 2
                HomeScreen(navController = navController, role = role,authViewModel = authViewModel)
            }
            // OnBoarding Screen
            composable(Screen.OnBoarding.route) {
                OnBoardingScreen(
                    modifier = Modifier,
                    navController = navController,
                    onLoginClick = { role ->
                        if (BuildConfig.DEBUG) {
                            navController.navigate(Screen.Home.chooseRole(role))
                        }else{
                        navController.navigate(Screen.Login.chooseRole(role))
                        }
                    },
                    authViewModel = authViewModel,
                    onNavigateToSignUp = {
                        Log.d("Navigation", "Navigating to SignUp")
                        navController.navigate(Screen.SignUp.route) {
                            popUpTo(Screen.OnBoarding.route) { inclusive = false }
                        }
                    },
                )
            }
            // SignUp Screen
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    onNavigateToOnBoarding = {
                        navController.navigate(Screen.OnBoarding.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = false }
                        }
                    },
                    authViewModel
                )
            }
            // UserProfile Screen
            composable(Screen.UserProfile.route) {
                UserProfileScreen(modifier = Modifier.fillMaxSize(),
                    authViewModel = authViewModel,
                    navController = navController)
            }
        }
    }
}



