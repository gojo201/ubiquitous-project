package com.tcsappdev.ubiquitous.domain.repository

import com.tcsappdev.ubiquitous.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<Workout>>

    suspend fun insertWorkout(workout: Workout)

    suspend fun deleteWorkout(workout: Workout)
}