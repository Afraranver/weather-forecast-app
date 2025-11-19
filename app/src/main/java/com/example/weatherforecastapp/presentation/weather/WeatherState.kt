package com.example.weatherforecastapp.presentation.weather

import com.example.weatherforecastapp.domain.model.Weather

sealed class WeatherState {
    object Initial : WeatherState()
    object Loading : WeatherState()
    data class Success(val weather: Weather) : WeatherState()
    data class Error(val message: String) : WeatherState()
}