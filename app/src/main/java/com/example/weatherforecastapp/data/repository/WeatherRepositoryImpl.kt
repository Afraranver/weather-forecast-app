package com.example.weatherforecastapp.data.repository

import com.example.weatherforecastapp.BuildConfig
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
import com.example.weatherforecastapp.domain.model.Weather
import com.example.weatherforecastapp.util.WeatherException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
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
    ): Flow<Weather> = flow {
        val cacheKey = "${lat}_$lon"

        // Try cache first if not forcing refresh
        if (!forceRefresh) {
            val cached = dao.getWeather(cacheKey).firstOrNull()
            cached?.let {
                val cacheAge = System.currentTimeMillis() - it.lastUpdated
                if (cacheAge < CACHE_TIMEOUT) {
                    Timber.d("Returning cached weather")
                    emit(it.toDomainModel())
                    return@flow // Exit early with cache
                }
            }
        }

        // Fetch from network
        Timber.d("Fetching from network")
        val response = api.getCurrentWeather(lat, lon, apiKey = BuildConfig.WEATHER_API_KEY)
        val weather = response.toDomainModel()

        // Cache and emit
        dao.insertWeather(WeatherEntity.fromDomainModel(weather, lat, lon))
        emit(weather)
    }.catch { e ->
        Timber.e(e, "Error fetching weather")
        throw WeatherException.fromThrowable(e)
    }

    override fun getForecast(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean
    ): Flow<List<Forecast>> = flow {
        Timber.d("Fetching forecast from API")
        val response = api.getForecast(lat, lon, apiKey = BuildConfig.WEATHER_API_KEY)
        emit(response.toDomainModel())
    }.catch { e ->
        Timber.e(e, "Error fetching forecast")
        throw when (e) {
            is IOException -> WeatherException.NetworkError(e)
            is HttpException -> WeatherException.ServerError(e.code(), e.message())
            else -> WeatherException.UnknownError(e)
        }
    }

    override suspend fun refreshWeather(lat: Double, lon: Double) {
        try {
            val response = api.getCurrentWeather(lat, lon, apiKey = BuildConfig.WEATHER_API_KEY)
            val weather = response.toDomainModel()
            dao.insertWeather(WeatherEntity.fromDomainModel(weather, lat, lon))
            Timber.d("Weather refreshed successfully")
        } catch (e: IOException) {
            throw WeatherException.NetworkError(e)
        } catch (e: HttpException) {
            throw WeatherException.ServerError(e.code(), e.message())
        } catch (e: Exception) {
            throw WeatherException.UnknownError(e)
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
        private const val CACHE_TIMEOUT = 10 * 60 * 1000L
    }
}