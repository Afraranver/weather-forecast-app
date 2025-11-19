package com.example.weatherforecastapp.domain.repository

import com.example.weatherforecastapp.domain.model.Forecast
import com.example.weatherforecastapp.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import com.example.weatherforecastapp.util.Resource

interface WeatherRepository {
    fun getCurrentWeather(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ): Flow<Resource<Weather>>

    fun getForecast(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ): Flow<Resource<List<Forecast>>>

    suspend fun refreshWeather(lat: Double, lon: Double)
}