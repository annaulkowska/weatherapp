package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.domain.model.Daily

data class DailyDto(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: FeelsLikeDto,
    val humidity: Int,
    val moon_phase: Double,
    val moonrise: Int,
    val moonset: Int,
    val pop: Double,
    val pressure: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: TempDto,
    val uvi: Double,
    val weather: List<WeatherXDto>,
    val wind_deg: Double,
    val wind_gust: Double,
    val wind_speed: Double
){
    fun toDaily():Daily{
        return Daily(
            clouds = clouds,
            dew_point = dew_point,
            dt = dt,
            feels_like = feels_like.toFeelsLike(),
            humidity = humidity,
            moon_phase = moon_phase,
            moonrise = moonrise,
            moonset = moonset,
            pop = pop,
            pressure = pressure,
            sunrise = sunrise,
            sunset = sunset,
            temp = temp.toTemp(),
            uvi = uvi,
            weather = weather.map { it.toWeatherX()},
            wind_deg = wind_deg,
            wind_gust = wind_gust,
            wind_speed = wind_speed
        )
    }
}