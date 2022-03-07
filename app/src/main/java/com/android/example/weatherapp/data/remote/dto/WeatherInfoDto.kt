package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.data.local.entity.WeatherInfoEntity

data class WeatherInfoDto(
    val alerts: List<AlertDto>?,
    val current: CurrentDto,
    val daily: List<DailyDto>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
){
    fun toWeatherInfoEntity() : WeatherInfoEntity {

        return WeatherInfoEntity(
            alerts = alerts?.map { it?.toAlert()}?: listOf(),
            current = current.toCurrent(),
            dailys = daily.map { it.toDaily() },
            lat = lat,
            lon = lon,
            timezone = timezone,
            timezone_offset = timezone_offset
        )
    }
}