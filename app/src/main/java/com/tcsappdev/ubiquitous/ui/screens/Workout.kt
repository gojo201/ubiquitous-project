package com.tcsappdev.ubiquitous.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.tcsappdev.ubiquitous.data.remote.RetrofitClient
import com.tcsappdev.ubiquitous.domain.model.WorkoutType
import com.tcsappdev.ubiquitous.domain.usecase.CalculateCalorieUseCase
import com.tcsappdev.ubiquitous.ui.viewmodel.WorkoutViewModel

@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    onWorkoutFinished: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val routePoints by viewModel.routePoints.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    val durationSeconds by viewModel.durationSeconds.collectAsStateWithLifecycle()
    val distanceKm by viewModel.distanceKm.collectAsStateWithLifecycle()

    val calorieUseCase = remember { CalculateCalorieUseCase() }

    val currentCalories = remember(durationSeconds) {
        calorieUseCase.execute(
            type = WorkoutType.RUNNING,
            durationMinutes = durationSeconds / 60,
            userWeightKg = 75.0
        )
    }

    var currentTemp by remember { mutableStateOf("--") }

    LaunchedEffect(routePoints.firstOrNull()) {
        val firstPoint = routePoints.firstOrNull()
        if (firstPoint != null && currentTemp == "--") {
            try {
                val response = RetrofitClient.weatherApiService.getWeather(
                    lat = firstPoint.latitude,
                    lng = firstPoint.longitude
                )
                currentTemp = response.current.temperature.toString()
            } catch (e: Exception) {
                currentTemp = "24.0"
            }
        }
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(46.5547, 15.6459), 15f)
    }

    LaunchedEffect(routePoints) {
        if (isTracking && routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(routePoints.last()),
                durationMs = 500
            )
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        if (granted) {
            viewModel.startWorkout(WorkoutType.RUNNING)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapLayer(
            cameraPositionState = cameraPositionState,
            routePoints = routePoints,
            isMyLocationEnabled = hasLocationPermission
        )
        if(isTracking)
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⛅", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${currentTemp}°C",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        WorkoutStats(
            distanceKm = distanceKm,
            durationSeconds = durationSeconds,
            calories = currentCalories,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
        )

        WorkoutControls(
            isTracking = isTracking,
            onStart = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onStop = {
                viewModel.stopAndSaveWorkout(WorkoutType.RUNNING)
                onWorkoutFinished()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun MapLayer(
    cameraPositionState: CameraPositionState,
    routePoints: List<LatLng>,
    isMyLocationEnabled: Boolean
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = isMyLocationEnabled),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        if (routePoints.isNotEmpty()) {
            Polyline(
                points = routePoints,
                color = Color(0xFFFF5722),
                width = 20f
            )
        }
    }
}

@Composable
private fun WorkoutStats(
    distanceKm: Double,
    durationSeconds: Int,
    calories: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Distance", value = String.format(Locale.US, "%.2f km", distanceKm))
            StatItem(label = "Duration", value = formatDuration(durationSeconds))
            StatItem(label = "Calories", value = String.format(Locale.US, "%.0f kcal", calories))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(
            value,
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun WorkoutControls(
    isTracking: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (!isTracking) {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .height(56.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Start Workout", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onStop,
                modifier = Modifier
                    .height(56.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Finish & Save", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}