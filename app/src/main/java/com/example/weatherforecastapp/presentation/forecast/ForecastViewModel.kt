package com.example.weatherforecastapp.presentation.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.domain.usecase.GetForecastUseCase
import com.example.weatherforecastapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ForecastState>(ForecastState.Initial)
    val state: StateFlow<ForecastState> = _state.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Default location (Colombo, Sri Lanka)
    private var currentLat = 6.9271
    private var currentLon = 79.8612

    init {
        loadForecast()
    }

    fun loadForecast(forceRefresh: Boolean = false){
        viewModelScope.launch {
            getForecastUseCase(currentLat, currentLon, forceRefresh)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { forecasts ->
                            _state.value = ForecastState.Success(forecasts)
                            Timber.d("Forecast loaded successfully: ${forecasts.size} items")
                        },
                        onFailure = { error ->
                            val message = error.message ?: "Something went wrong!!"
                            _state.value = ForecastState.Error(message)
                            _snackbarMessage.emit(message)
                            Timber.e(error, "Error loading forecast")
                        }
                    )

            }
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon
        loadForecast(forceRefresh = true)
    }

    fun retry() {
        loadForecast(forceRefresh = true)
    }
}