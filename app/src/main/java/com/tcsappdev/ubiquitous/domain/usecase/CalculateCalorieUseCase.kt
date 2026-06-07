package com.tcsappdev.ubiquitous.domain.usecase

import com.tcsappdev.ubiquitous.domain.model.WorkoutType

class CalculateCalorieUseCase {

    fun execute(type: WorkoutType, durationMinutes: Int, userWeightKg: Double = 75.0): Double {
        if (durationMinutes <= 0 || userWeightKg <= 0) return 0.0

        val durationHours = durationMinutes / 60.0
        val metValue = when (type) {
            WorkoutType.RUNNING -> 9.8
            WorkoutType.WALKING -> 3.8
            WorkoutType.CYCLING -> 7.5
        }

        val result = metValue * userWeightKg * durationHours
        return String.format("%.2f", result).replace(",", ".").toDouble()
    }
}