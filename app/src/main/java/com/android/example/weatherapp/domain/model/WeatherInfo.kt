package com.android.example.weatherapp.domain.model

data class WeatherInfo(
    val current: Current,
    val dailys: List<Daily>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)