package com.example.weatherforecastapp.domain.usecase

import com.example.weatherforecastapp.domain.model.Forecast
import com.example.weatherforecastapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.weatherforecastapp.util.Resource

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ): Flow<Resource<List<Forecast>>> {
        return repository.getForecast(lat, lon, forceRefresh)
    }
}