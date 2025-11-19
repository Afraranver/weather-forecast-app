package com.example.weatherforecastapp.domain.model

data class Weather(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val pressure: Int,
    val humidity: Int,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)