package com.tcsappdev.ubiquitous.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String){
    object Login: Screen("login")
    object Register: Screen("register")
    object Home: Screen("home")
    object Profile: Screen("profile")
    object Workout: Screen("workout")
}

@Composable
fun NavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ){
        composable(Screen.Login.route){}
        composable(Screen.Register.route){}
        composable(Screen.Home.route){}
        composable(Screen.Profile.route){}
        composable(Screen.Workout.route){}
    }
}