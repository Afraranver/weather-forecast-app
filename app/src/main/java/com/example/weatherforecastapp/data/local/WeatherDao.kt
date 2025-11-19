package com.example.weatherforecastapp.data.local

import com.example.weatherforecastapp.data.local.entity.WeatherEntity
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE id = :id")
    fun getWeather(id: String): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather WHERE id = :id")
    suspend fun deleteWeather(id: String)

    @Query("DELETE FROM weather")
    suspend fun deleteAllWeather()

    @Query("SELECT * FROM weather ORDER BY lastUpdated DESC LIMIT 1")
    fun getLatestWeather(): Flow<WeatherEntity?>
}