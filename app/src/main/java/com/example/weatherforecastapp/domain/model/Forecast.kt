package com.example.weatherforecastapp.domain.model

data class Forecast(
    val date: Long,
    val temperature: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double
)