package com.example.weatherforecastapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list")
    val list: List<ForecastItem>,
    @SerializedName("city")
    val city: City
)

data class ForecastItem(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<WeatherInfo>,
    @SerializedName("wind")
    val wind: Wind
)

data class City(
    @SerializedName("name")
    val name: String,
    @SerializedName("coord")
    val coord: Coord
)