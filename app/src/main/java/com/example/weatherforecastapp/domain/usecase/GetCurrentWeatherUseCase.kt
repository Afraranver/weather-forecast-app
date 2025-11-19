package com.example.weatherforecastapp.domain.usecase

import com.example.weatherforecastapp.domain.model.Weather
import com.example.weatherforecastapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.weatherforecastapp.util.Resource

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ): Flow<Resource<Weather>> {
        return repository.getCurrentWeather(lat, lon, forceRefresh)
    }
}