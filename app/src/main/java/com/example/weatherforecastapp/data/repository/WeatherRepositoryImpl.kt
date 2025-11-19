package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.data.local.WeatherDao
import com.example.weatherforecastapp.data.local.entity.WeatherEntity
import com.example.weatherforecastapp.data.remote.WeatherApi
import com.example.weatherforecastapp.data.remote.dto.ForecastResponse
import com.example.weatherforecastapp.data.remote.dto.WeatherResponse
import com.example.weatherforecastapp.domain.model.Forecast
import com.example.weatherforecastapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

import com.example.weatherforecastapp.BuildConfig
import com.example.weatherforecastapp.domain.model.Weather
import com.example.weatherforecastapp.util.Resource
import retrofit2.HttpException
import java.io.IOException


class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao
) : WeatherRepository {

    override fun getCurrentWeather(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean
    ): Flow<Resource<Weather>> = flow {
        emit(Resource.Loading())

        val cacheKey = "${lat}_$lon"

        // Emit cached data first (if not forcing refresh)
        if (!forceRefresh) {
            dao.getWeather(cacheKey).collect { cachedWeather ->
                cachedWeather?.let {
                    // Check if cache is recent (less than 10 minutes old)
                    val cacheAge = System.currentTimeMillis() - it.lastUpdated
                    if (cacheAge < CACHE_TIMEOUT) {
                        Timber.d("Emitting cached weather data")
                        emit(Resource.Success(it.toDomainModel()))
                        return@collect
                    }
                }
            }
        }

        // Fetch fresh data from API
        try {
            Timber.d("Fetching weather from API for lat: $lat, lon: $lon")
            val response = api.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY
            )

            val weather = response.toDomainModel()

            // Cache the result
            dao.insertWeather(WeatherEntity.fromDomainModel(weather, lat, lon))

            Timber.d("Successfully fetched and cached weather data")
            emit(Resource.Success(weather))

        } catch (e: HttpException) {
            Timber.e(e, "HTTP error fetching weather")
            emit(Resource.Error(
                message = "Server error: ${e.message()}",
                data = null
            ))
        } catch (e: IOException) {
            Timber.e(e, "Network error fetching weather")
            emit(Resource.Error(
                message = "Network error. Please check your connection.",
                data = null
            ))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error fetching weather")
            emit(Resource.Error(
                message = "An unexpected error occurred: ${e.message}",
                data = null
            ))
        }
    }

    override fun getForecast(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean
    ): Flow<Resource<List<Forecast>>> = flow {
        emit(Resource.Loading())

        try {
            Timber.d("Fetching forecast from API for lat: $lat, lon: $lon")
            val response = api.getForecast(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY
            )

            val forecasts = response.toDomainModel()

            Timber.d("Successfully fetched ${forecasts.size} forecast items")
            emit(Resource.Success(forecasts))

        } catch (e: HttpException) {
            Timber.e(e, "HTTP error fetching forecast")
            emit(Resource.Error(
                message = "Server error: ${e.message()}",
                data = null
            ))
        } catch (e: IOException) {
            Timber.e(e, "Network error fetching forecast")
            emit(Resource.Error(
                message = "Network error. Please check your connection.",
                data = null
            ))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error fetching forecast")
            emit(Resource.Error(
                message = "An unexpected error occurred: ${e.message}",
                data = null
            ))
        }
    }

    override suspend fun refreshWeather(lat: Double, lon: Double) {
        try {
            val response = api.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            val weather = response.toDomainModel()
            dao.insertWeather(WeatherEntity.fromDomainModel(weather, lat, lon))
            Timber.d("Weather refreshed successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing weather")
            throw e
        }
    }

    private fun WeatherResponse.toDomainModel(): Weather {
        return Weather(
            cityName = name,
            temperature = main.temp,
            feelsLike = main.feelsLike,
            minTemp = main.tempMin,
            maxTemp = main.tempMax,
            pressure = main.pressure,
            humidity = main.humidity,
            description = weather.firstOrNull()?.description ?: "",
            icon = weather.firstOrNull()?.icon ?: "",
            windSpeed = wind.speed,
            cloudiness = clouds.all,
            sunrise = sys.sunrise,
            sunset = sys.sunset,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun ForecastResponse.toDomainModel(): List<Forecast> {
        return list.map { item ->
            Forecast(
                date = item.dt,
                temperature = item.main.temp,
                minTemp = item.main.tempMin,
                maxTemp = item.main.tempMax,
                description = item.weather.firstOrNull()?.description ?: "",
                icon = item.weather.firstOrNull()?.icon ?: "",
                humidity = item.main.humidity,
                windSpeed = item.wind.speed
            )
        }
    }

    companion object {
        private const val CACHE_TIMEOUT = 10 * 60 * 1000L // 10 minutes
    }
}