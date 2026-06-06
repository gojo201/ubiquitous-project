package com.tcsappdev.ubiquitous.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tcsappdev.ubiquitous.ui.screens.HomeScreen
import com.tcsappdev.ubiquitous.ui.screens.LoginScreen
import com.tcsappdev.ubiquitous.ui.screens.ProfileScreen
import com.tcsappdev.ubiquitous.ui.screens.RegisterScreen
import com.tcsappdev.ubiquitous.ui.screens.WorkoutScreen


sealed class Screen(val route: String){
    object Login: Screen("login")
    object Register: Screen("register")
    object Home: Screen("home")
    object Profile: Screen("profile")
    object Workout: Screen("workout")
}

@Composable
fun NavGraph(modifier: Modifier = Modifier){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ){
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
            ProfileScreen(modifier, navController)
        }
        composable(Screen.Workout.route){
            WorkoutScreen(modifier, navController)
        }
    }
}