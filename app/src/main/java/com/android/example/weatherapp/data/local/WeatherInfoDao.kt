package com.android.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.example.weatherapp.data.local.entity.WeatherInfoEntity

@Dao
interface WeatherInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherInfos(weather: WeatherInfoEntity)

    @Query("DELETE FROM weatherinfoentity WHERE :lat = lat AND :lon = lat")
    suspend fun deleteWeatherInfo(lat: Double, lon: Double)

    @Query("SELECT * FROM weatherinfoentity WHERE :lat = lat AND :lon = lon")
    suspend fun getWeatherInfo(lat: Double, lon: Double): WeatherInfoEntity
}