package com.tcsappdev.ubiquitous.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.tcsappdev.ubiquitous.domain.model.Workout
import com.tcsappdev.ubiquitous.domain.model.WorkoutType
import com.tcsappdev.ubiquitous.domain.usecase.CalculateCalorieUseCase
import com.tcsappdev.ubiquitous.ui.navigation.Screen
import com.tcsappdev.ubiquitous.ui.theme.Spacing
import com.tcsappdev.ubiquitous.ui.viewmodel.AuthViewModel
import com.tcsappdev.ubiquitous.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onNavigateToDetail: (String) -> Unit, // 🔥 HATA VEREN EKSİK PARAMETRE BURAYA EKLENDİ 🔥
    authViewModel: AuthViewModel = viewModel(),
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "there"

    val pastWorkouts by workoutViewModel.pastWorkouts.collectAsStateWithLifecycle(initialValue = emptyList())
    val calorieUseCase = remember { CalculateCalorieUseCase() }

    val totalDistance = pastWorkouts.sumOf { it.distanceKm }
    val totalWorkouts = pastWorkouts.size
    val totalCalories = pastWorkouts.sumOf { workout ->
        try {
            calorieUseCase.execute(
                type = WorkoutType.valueOf(workout.type),
                durationMinutes = workout.durationMinutes,
                userWeightKg = 75.0
            )
        } catch (e: Exception) { 0.0 }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hello,", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(Spacing.small))
                Text(userName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(Spacing.extraLarge))

        Text("Your Stats", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = Spacing.large))
        Spacer(modifier = Modifier.height(Spacing.small))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.large), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatCard("Distance", String.format(Locale.US, "%.1f km", totalDistance))
            StatCard("Workouts", "$totalWorkouts")
            StatCard("Calories", String.format(Locale.US, "%.0f kcal", totalCalories))
        }

        Spacer(modifier = Modifier.height(Spacing.huge))

        Button(
            onClick = { navController.navigate(Screen.Workout.route) { launchSingleTop = true } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.large).height(58.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Start Workout", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(Spacing.small))
            Text("Start Workout", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(Spacing.huge))
        Text("Recent Activities", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = Spacing.large))
        Spacer(modifier = Modifier.height(Spacing.small))

        if (pastWorkouts.isEmpty()) {
            Text("No activities yet. Tap 'Start Workout'!", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.padding(Spacing.large))
        } else {
            pastWorkouts.take(10).forEach { workout ->
                ActivityCard(workout = workout, onClick = { onNavigateToDetail(workout.id) }) // 🔥 TIKLAMA BURADAN GÖNDERİLİYOR
                Spacer(modifier = Modifier.height(Spacing.small))
            }
        }
        Spacer(modifier = Modifier.height(Spacing.extraLarge))
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun ActivityCard(workout: Workout, onClick: () -> Unit) { // 🔥 KARTA TIKLAMA ÖZELLİĞİ EKLENDİ
    val workoutType = try { WorkoutType.valueOf(workout.type) } catch (e: Exception) { WorkoutType.RUNNING }
    val icon = when (workoutType) {
        WorkoutType.RUNNING -> Icons.AutoMirrored.Filled.DirectionsRun
        WorkoutType.CYCLING -> Icons.Default.PedalBike
        WorkoutType.WALKING -> Icons.AutoMirrored.Filled.DirectionsWalk
    }
    val sdf = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.US) }
    val dateString = sdf.format(Date(workout.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.large)
            .clickable { onClick() }, // 🔥 ARTIK KARTLAR TIKLANABİLİR!
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(Spacing.medium), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = workout.type, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(Spacing.medium))
                Column {
                    Text(workout.type.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(String.format(Locale.US, "%.2f km • %d min", workout.distanceKm, workout.durationMinutes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(dateString, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                workout.weatherTemp?.let { temp ->
                    Text("${temp}°C", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}