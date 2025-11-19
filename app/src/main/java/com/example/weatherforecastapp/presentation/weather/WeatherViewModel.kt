package com.example.weatherforecastapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.domain.usecase.GetCurrentWeatherUseCase
import com.example.weatherforecastapp.domain.usecase.RefreshWeatherUseCase
import com.example.weatherforecastapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<WeatherState>(WeatherState.Initial)
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Default location (Colombo, Sri Lanka)
    private var currentLat = 6.9271
    private var currentLon = 79.8612

    init {
        loadWeather()
    }

    fun loadWeather(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getCurrentWeatherUseCase(currentLat, currentLon, forceRefresh)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _state.value = WeatherState.Loading
                            Timber.d("Loading weather data...")
                        }
                        is Resource.Success -> {
                            result.data?.let { weather ->
                                _state.value = WeatherState.Success(weather)
                                Timber.d("Weather loaded successfully: ${weather.cityName}")
                            }
                        }
                        is Resource.Error -> {
                            _state.value = WeatherState.Error(
                                result.message ?: "An unexpected error occurred"
                            )
                            Timber.e("Error loading weather: ${result.message}")
                            _snackbarMessage.emit(result.message ?: "Error loading weather")
                        }
                    }
                }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshWeatherUseCase(currentLat, currentLon)
                loadWeather(forceRefresh = true)
                _snackbarMessage.emit("Weather updated")
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing weather")
                _snackbarMessage.emit("Failed to refresh weather")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon
        loadWeather(forceRefresh = true)
    }

    fun retry() {
        loadWeather(forceRefresh = true)
    }
}