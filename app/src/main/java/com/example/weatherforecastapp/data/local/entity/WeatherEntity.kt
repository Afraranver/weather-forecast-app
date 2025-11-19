package com.example.weatherforecastapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecastapp.domain.model.Weather

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey
    val id: String, // Composite of lat-lon
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val pressure: Int,
    val humidity: Int,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val lastUpdated: Long
) {
    fun toDomainModel(): Weather {
        return Weather(
            cityName = cityName,
            temperature = temperature,
            feelsLike = feelsLike,
            minTemp = minTemp,
            maxTemp = maxTemp,
            pressure = pressure,
            humidity = humidity,
            description = description,
            icon = icon,
            windSpeed = windSpeed,
            cloudiness = cloudiness,
            sunrise = sunrise,
            sunset = sunset,
            lastUpdated = lastUpdated
        )
    }

    companion object {
        fun fromDomainModel(weather: Weather, lat: Double, lon: Double): WeatherEntity {
            return WeatherEntity(
                id = "${lat}_$lon",
                cityName = weather.cityName,
                temperature = weather.temperature,
                feelsLike = weather.feelsLike,
                minTemp = weather.minTemp,
                maxTemp = weather.maxTemp,
                pressure = weather.pressure,
                humidity = weather.humidity,
                description = weather.description,
                icon = weather.icon,
                windSpeed = weather.windSpeed,
                cloudiness = weather.cloudiness,
                sunrise = weather.sunrise,
                sunset = weather.sunset,
                lastUpdated = weather.lastUpdated
            )
        }
    }
}