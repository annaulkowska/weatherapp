package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.domain.model.Current

data class CurrentDto(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    val weather: List<WeatherDto>,
    val wind_deg: Int,
    val wind_speed: Double
){
    fun toCurrent(): Current{
        return Current(
            clouds = clouds,
            dew_point = dew_point,
            dt = dt,
            feels_like = feels_like,
            humidity = humidity,
            pressure = pressure,
            sunrise = sunrise,
            sunset = sunset,
            temp = temp,
            uvi = uvi,
            visibility = visibility/1000,
            weather = weather.map { it.toWeather()},
            wind_deg = wind_deg,
            wind_speed = wind_speed
        )
    }
}