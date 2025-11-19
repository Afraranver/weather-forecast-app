package com.example.weatherforecastapp.presentation.forecast

import com.example.weatherforecastapp.domain.model.Forecast

sealed class ForecastState {
    object Initial : ForecastState()
    object Loading : ForecastState()
    data class Success(val forecasts: List<Forecast>) : ForecastState()
    data class Error(val message: String) : ForecastState()
}