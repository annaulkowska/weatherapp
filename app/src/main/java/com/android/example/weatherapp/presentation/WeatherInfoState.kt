package com.android.example.weatherapp.presentation

import com.android.example.weatherapp.domain.model.CityBtnModel
import com.android.example.weatherapp.domain.model.WeatherInfo

data class WeatherInfoState(
    val cityBtnList: List<CityBtnModel> = emptyList(),
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false
)