package com.example.weatherforecastapp.domain.usecase

import com.example.weatherforecastapp.domain.model.Forecast
import com.example.weatherforecastapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.weatherforecastapp.util.Resource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ): Flow<Result<List<Forecast>>> {
        return repository.getForecast(lat, lon, forceRefresh)
            .map { forecasts -> Result.success(forecasts) }
            .catch { e -> emit(Result.failure(e)) }
    }
}