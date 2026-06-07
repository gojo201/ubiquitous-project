package com.tcsappdev.ubiquitous.domain.model

import java.util.UUID

data class Workout(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val type: String = WorkoutType.RUNNING.name,
    val distanceKm: Double = 0.0,
    val durationMinutes: Int = 0,
    val weatherTemp: Double? = null,
    val routePoints: List<LocationPoint> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)