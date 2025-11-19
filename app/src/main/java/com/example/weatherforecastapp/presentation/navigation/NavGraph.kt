package com.example.weatherforecastapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherforecastapp.presentation.forecast.ForecastScreen
import com.example.weatherforecastapp.presentation.weather.WeatherScreen

sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object Forecast : Screen("forecast")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route
    ) {
        composable(Screen.Weather.route) {
            WeatherScreen(
                onNavigateToForecast = {
                    navController.navigate(Screen.Forecast.route)
                }
            )
        }

        composable(Screen.Forecast.route) {
            ForecastScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}