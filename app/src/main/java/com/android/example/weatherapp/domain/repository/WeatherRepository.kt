package com.android.example.weatherapp.domain.repository

import com.android.example.weatherapp.core.util.Resource
import com.android.example.weatherapp.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherInfo(lat: Double, lon: Double, exclude: String, units: String, appid: String): Flow<Resource<WeatherInfo>>
}