package com.tcsappdev.ubiquitous.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.tcsappdev.ubiquitous.domain.model.WorkoutType
import com.tcsappdev.ubiquitous.domain.usecase.CalculateCalorieUseCase
import com.tcsappdev.ubiquitous.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    onBackClick: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val pastWorkouts by viewModel.pastWorkouts.collectAsStateWithLifecycle(initialValue = emptyList())
    val workout = remember(pastWorkouts) { pastWorkouts.find { it.id == workoutId } }

    val calorieUseCase = remember { CalculateCalorieUseCase() }
    val sdf = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.US) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (workout == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val calories = remember(workout) {
                try {
                    calorieUseCase.execute(
                        type = WorkoutType.valueOf(workout.type),
                        durationMinutes = workout.durationMinutes,
                        userWeightKg = 75.0
                    )
                } catch (e: Exception) { 0.0 }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f)
                ) {
                    if (workout.routePoints.isNotEmpty()) {
                        val startPoint = LatLng(workout.routePoints.first().latitude, workout.routePoints.first().longitude)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(startPoint, 15f)
                        }

                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            uiSettings = MapUiSettings(zoomControlsEnabled = true)
                        ) {
                            val mapPoints = workout.routePoints.map { LatLng(it.latitude, it.longitude) }
                            Polyline(
                                points = mapPoints,
                                color = MaterialTheme.colorScheme.primary,
                                width = 14f
                            )
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No map/route data available for this activity.", color = Color.DarkGray)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = workout.type.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 24.sp,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = sdf.format(Date(workout.timestamp)), fontSize = 14.sp, color = Color.Gray)
                            }

                            workout.weatherTemp?.let { temp ->
                                Text(
                                    text = "${temp}°C",
                                    fontSize = 16.sp,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .background(Color(0xFFE1F5FE), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = Color(0xFF0288D1)
                                )
                            }
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailStatItem(label = "Distance", value = String.format(Locale.US, "%.2f km", workout.distanceKm))
                            DetailStatItem(label = "Duration", value = "${workout.durationMinutes} min")
                            DetailStatItem(label = "Calories", value = String.format(Locale.US, "%.0f kcal", calories))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 20.sp, style = MaterialTheme.typography.titleMedium)
    }
}