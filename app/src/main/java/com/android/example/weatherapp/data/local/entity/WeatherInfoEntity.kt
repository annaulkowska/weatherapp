package com.android.example.weatherapp.data.local.entity

import androidx.room.Entity
import com.android.example.weatherapp.domain.model.Current
import com.android.example.weatherapp.domain.model.Daily
import com.android.example.weatherapp.domain.model.WeatherInfo

@Entity(primaryKeys = ["lat", "lon"])
data class WeatherInfoEntity(
    val current: Current,
    val dailys: List<Daily>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int){

    fun toWeatherInfo() : WeatherInfo{

        return WeatherInfo(
            current = current,
            dailys = dailys,
            lat = lat,
            lon = lon,
            timezone = timezone,
            timezone_offset = timezone_offset
        )
    }
}

