package com.tcsappdev.ubiquitous.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestoreSettings
import com.tcsappdev.ubiquitous.data.remote.RetrofitClient
import com.tcsappdev.ubiquitous.domain.model.Workout
import com.tcsappdev.ubiquitous.domain.repository.WorkoutRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseWorkoutRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // Sizin sisteme uyumlu hale getirildi
) : WorkoutRepository {

    init {
        db.firestoreSettings = firestoreSettings { isPersistenceEnabled = true }
    }

    private val workoutsCollection = db.collection("workouts")

    override fun getAllWorkouts(): Flow<List<Workout>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val subscription = workoutsCollection
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val workouts = snapshot.documents.mapNotNull { it.toObject(Workout::class.java) }
                    trySend(workouts).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun insertWorkout(workout: Workout) {
        val currentUserId = auth.currentUser?.uid ?: return

        var temperature: Double? = null
        if (workout.routePoints.isNotEmpty()) {
            try {
                val firstPoint = workout.routePoints.first()
                val response = RetrofitClient.weatherApiService.getWeather(
                    lat = firstPoint.latitude,
                    lng = firstPoint.longitude
                )
                temperature = response.current.temperature
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val finalWorkout = workout.copy(userId = currentUserId, weatherTemp = temperature)
        workoutsCollection.document(finalWorkout.id).set(finalWorkout).await()
    }

    override suspend fun deleteWorkout(workout: Workout) {
        workoutsCollection.document(workout.id).delete().await()
    }
}