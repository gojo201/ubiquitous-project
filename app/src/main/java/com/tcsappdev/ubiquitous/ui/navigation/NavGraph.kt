package com.tcsappdev.ubiquitous.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tcsappdev.ubiquitous.ui.screens.HomeScreen
import com.tcsappdev.ubiquitous.ui.screens.LoginScreen
import com.tcsappdev.ubiquitous.ui.screens.ProfileScreen
import com.tcsappdev.ubiquitous.ui.screens.RegisterScreen
import com.tcsappdev.ubiquitous.ui.screens.WelcomeScreen
import com.tcsappdev.ubiquitous.ui.screens.WorkoutScreen
import com.tcsappdev.ubiquitous.ui.screens.WorkoutDetailScreen // EKLENDİ
import com.tcsappdev.ubiquitous.ui.viewmodel.AuthState
import com.tcsappdev.ubiquitous.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String){
    object Login: Screen("login")
    object Register: Screen("register")
    object Home: Screen("home")
    object Profile: Screen("profile")
    object Workout: Screen("workout")
    object Welcome: Screen("welcome")

    object WorkoutDetail: Screen("workout_detail/{workoutId}") {
        fun createRoute(workoutId: String) = "workout_detail/$workoutId"
    }
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    val startDestination = if (authState is AuthState.Authenticated) {
        Screen.Home.route
    } else {
        Screen.Welcome.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Detay sayfası bu listede OLMADIĞI için, eski koşu detayına girince alt menü otomatik gizlenecek!
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Workout.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                Screen.Welcome.route,
                enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }) {
                WelcomeScreen(modifier, navController)
            }

            composable(
                Screen.Login.route,
                enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }) {
                LoginScreen(modifier, navController)
            }

            composable(
                Screen.Register.route,
                enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }) {
                RegisterScreen(modifier, navController)
            }

            composable(
                Screen.Home.route,
                enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }) {
                HomeScreen(
                    modifier = modifier,
                    navController = navController,
                    onNavigateToDetail = { workoutId ->
                        // Geçmişteki antrenmana tıklandığında ID'sini alıp detay sayfasına fırlatıyor
                        navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                    }
                )
            }

            composable(
                Screen.Profile.route,
                enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }) {
                ProfileScreen(
                    modifier = modifier,
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle
                )
            }

            composable(
                Screen.Workout.route,
                enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }) {
                WorkoutScreen(
                    modifier = modifier,
                    onWorkoutFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Workout.route) { inclusive = true }
                        }
                    }
                )
            }

            // 🔥 YENİ: Firebase'den Çekilen Eski Antrenman Detay Sayfası
            composable(
                route = Screen.WorkoutDetail.route,
                enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }
            ) { backStackEntry ->
                // Rotadan gelen ID'yi yakalıyoruz
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""

                WorkoutDetailScreen(
                    workoutId = workoutId,
                    onBackClick = { navController.popBackStack() } // Geri tuşuna basınca Ana Ekrana (Home) döner
                )
            }
        }
    }
}