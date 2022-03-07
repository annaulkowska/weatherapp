package com.android.example.weatherapp.domain.use_case

import com.android.example.weatherapp.core.util.Gps
import com.android.example.weatherapp.core.util.WeatherUtils
import com.android.example.weatherapp.domain.model.WeatherInfo
import com.android.example.weatherapp.domain.repository.WeatherRepository
import com.plcoding.dictionary.core.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeather @Inject constructor( private val repository: WeatherRepository) {

    operator fun invoke(gps: Gps): Flow<Resource<WeatherInfo>> {
        return repository.getWeatherInfo(gps.lat, gps.lon, WeatherUtils.EXCLUDE, WeatherUtils.UNITS, WeatherUtils.API_ID)
    }
}