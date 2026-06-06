package com.tcsappdev.ubiquitous.ui.navigation

import android.nfc.tech.IsoDep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tcsappdev.ubiquitous.ui.screens.HomeScreen
import com.tcsappdev.ubiquitous.ui.screens.LoginScreen
import com.tcsappdev.ubiquitous.ui.screens.ProfileScreen
import com.tcsappdev.ubiquitous.ui.screens.RegisterScreen
import com.tcsappdev.ubiquitous.ui.screens.WelcomeScreen
import com.tcsappdev.ubiquitous.ui.screens.WorkoutScreen
import com.tcsappdev.ubiquitous.ui.viewmodel.AuthState
import com.tcsappdev.ubiquitous.ui.viewmodel.AuthViewModel


sealed class Screen(val route: String){
    object Login: Screen("login")
    object Register: Screen("register")
    object Home: Screen("home")
    object Profile: Screen("profile")
    object Workout: Screen("workout")
    object Welcome: Screen("welcome")
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
    ){
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    val startDestination = if(authState is AuthState.Authenticated) {
        Screen.Home.route
    } else {
        Screen.Welcome.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ){
        composable(Screen.Welcome.route){
            WelcomeScreen(modifier, navController)
        }
        composable(Screen.Login.route){
            LoginScreen(modifier, navController)
        }
        composable(Screen.Register.route){
            RegisterScreen(modifier, navController)
        }
        composable(Screen.Home.route){
            HomeScreen(modifier, navController)
        }
        composable(Screen.Profile.route){
            ProfileScreen(
                modifier = modifier,
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle)
        }
        composable(Screen.Workout.route){
            WorkoutScreen(modifier, navController)
        }
    }
}