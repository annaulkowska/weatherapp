package com.android.example.weatherapp.presentation

import com.android.example.weatherapp.core.util.Gps

sealed class WeatherEvent {
    data class onGetGpsCoordinates(val gps: Gps) : WeatherEvent()
}