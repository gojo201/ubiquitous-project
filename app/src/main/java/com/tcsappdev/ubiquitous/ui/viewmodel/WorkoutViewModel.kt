package com.tcsappdev.ubiquitous.ui.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.tcsappdev.ubiquitous.data.repository.FirebaseWorkoutRepository
import com.tcsappdev.ubiquitous.domain.model.LocationPoint
import com.tcsappdev.ubiquitous.domain.model.Workout
import com.tcsappdev.ubiquitous.domain.model.WorkoutType
import com.tcsappdev.ubiquitous.domain.usecase.CalculateCalorieUseCase
import com.tcsappdev.ubiquitous.utils.LocationObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseWorkoutRepository()
    private val calorieUseCase = CalculateCalorieUseCase()
    private val locationObserver = LocationObserver(application)

    private var locationJob: Job? = null
    private var timerJob: Job? = null

    val pastWorkouts: StateFlow<List<Workout>> = repository.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm.asStateFlow()

    fun startWorkout(type: WorkoutType) {
        _isTracking.value = true
        _routePoints.value = emptyList()
        _durationSeconds.value = 0
        _distanceKm.value = 0.0

        locationJob = locationObserver.getLocationUpdates(1000L)
            .onEach { latLng ->
                val currentList = _routePoints.value.toMutableList()
                if (currentList.isNotEmpty()) {
                    val lastPoint = currentList.last()
                    _distanceKm.value += calculateHaversineDistance(lastPoint, latLng)
                }
                currentList.add(latLng)
                _routePoints.value = currentList
            }.launchIn(viewModelScope)

        timerJob = viewModelScope.launch {
            while (_isTracking.value) {
                delay(1000L)
                _durationSeconds.value += 1
            }
        }
    }

    fun stopAndSaveWorkout(type: WorkoutType) {
        _isTracking.value = false
        locationJob?.cancel()
        timerJob?.cancel()

        viewModelScope.launch {
            val points = _routePoints.value.map { LocationPoint(it.latitude, it.longitude) }
            val durationMinutes = ceil(_durationSeconds.value / 60.0).toInt()

            val workout = Workout(
                type = type.name,
                distanceKm = round(_distanceKm.value * 100) / 100.0, // 2 basamak yuvarla
                durationMinutes = if (durationMinutes == 0) 1 else durationMinutes,
                routePoints = points,
                timestamp = System.currentTimeMillis()
            )
            repository.insertWorkout(workout)
            resetTrackingState()
        }
    }

    private fun resetTrackingState() {
        _routePoints.value = emptyList()
        _durationSeconds.value = 0
        _distanceKm.value = 0.0
    }

    private fun calculateHaversineDistance(p1: LatLng, p2: LatLng): Double {
        val r = 6371.0
        val dLat = Math.toRadians(p2.latitude - p1.latitude)
        val dLng = Math.toRadians(p2.longitude - p1.longitude)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}