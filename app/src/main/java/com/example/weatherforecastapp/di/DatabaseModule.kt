package com.example.weatherforecastapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherforecastapp.data.local.WeatherDao
import com.example.weatherforecastapp.data.local.WeatherDatabase
import com.example.weatherforecastapp.data.remote.WeatherApi
import com.example.weatherforecastapp.data.repository.WeatherRepositoryImpl
import com.example.weatherforecastapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao()
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        dao: WeatherDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(api, dao)
    }
}