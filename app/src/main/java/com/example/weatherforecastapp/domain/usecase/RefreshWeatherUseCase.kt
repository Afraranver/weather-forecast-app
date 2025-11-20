package com.example.weatherforecastapp.domain.usecase

import com.example.weatherforecastapp.domain.repository.WeatherRepository
import javax.inject.Inject

class RefreshWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Unit> {
        return try {
            repository.refreshWeather(lat, lon)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}