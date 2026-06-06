package com.tcsappdev.ubiquitous.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast?current=temperature_2m")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lng: Double
    ): WeatherResponse
}

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeatherData
)

data class CurrentWeatherData(
    @SerializedName("temperature_2m") val temperature: Double
)