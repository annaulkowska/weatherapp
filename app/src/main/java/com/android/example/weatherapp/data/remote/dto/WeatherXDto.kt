package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.domain.model.WeatherX

data class WeatherXDto(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
){
    fun toWeatherX(): WeatherX {
        return WeatherX(
            description = description,
            icon = icon,
            id = id,
            main = main
        )
    }
}