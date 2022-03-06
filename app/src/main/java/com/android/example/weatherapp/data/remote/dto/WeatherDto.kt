package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.domain.model.Weather

data class WeatherDto(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
){
    fun toWeather():Weather{
        return Weather(
            description = description,
            icon = icon,
            id = id,
            main = main
        )
    }
}