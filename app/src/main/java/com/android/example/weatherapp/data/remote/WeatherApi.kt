package com.android.example.weatherapp.data.remote

import com.android.example.weatherapp.data.remote.dto.WeatherInfoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    companion object {
        const val BASE_URL: String = "https://api.openweathermap.org"
    }

    @GET("/data/2.5/onecall?")
    suspend fun getWeatherInfo(
      @Query("lat") lat: Double,
      @Query("lon") lon: Double,
      @Query("exclude") exclude: String,
      @Query("units") units: String,
      @Query("appid") appid: String
    ):WeatherInfoDto
}