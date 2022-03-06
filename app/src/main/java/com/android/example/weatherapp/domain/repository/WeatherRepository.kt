package com.android.example.weatherapp.domain.repository

import com.android.example.weatherapp.domain.model.WeatherInfo
import com.plcoding.dictionary.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getWeatherInfo(lat: Double, lon: Double, exclude: String, units: String, appid: String): Flow<Resource<WeatherInfo>>
}