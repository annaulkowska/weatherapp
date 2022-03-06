package com.android.example.weatherapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.example.weatherapp.domain.model.Alert
import com.android.example.weatherapp.domain.model.Current
import com.android.example.weatherapp.domain.model.Daily
import com.android.example.weatherapp.domain.model.WeatherInfo

@Entity(primaryKeys = ["lat", "lon"])
data class WeatherInfoEntity(
    val alerts: List<Alert>,
    val current: Current,
    val dailys: List<Daily>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int){

    fun toWeatherInfo() : WeatherInfo{

        return WeatherInfo(
            alerts = alerts,
            current = current,
            dailys = dailys,
            lat = lat,
            lon = lon,
            timezone = timezone,
            timezone_offset = timezone_offset
        )
    }
}

