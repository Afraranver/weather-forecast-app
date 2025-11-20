package com.example.weatherforecastapp.util

import retrofit2.HttpException
import java.io.IOException

sealed class WeatherException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkError(cause: Throwable) : WeatherException("Network error", cause)
    class ServerError(val code: Int, message: String) : WeatherException(message)
    class UnknownError(cause: Throwable) : WeatherException("Unknown error", cause)

    companion object {
        fun fromThrowable(throwable: Throwable): WeatherException {
            return when (throwable) {
                is IOException -> NetworkError(throwable)
                is HttpException -> ServerError(throwable.code(), throwable.message())
                else -> UnknownError(throwable)
            }
        }
    }
}