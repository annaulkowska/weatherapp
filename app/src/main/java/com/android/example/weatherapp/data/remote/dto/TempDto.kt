package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.domain.model.Temp

data class TempDto(
    val day: Double,
    val eve: Double,
    val max: Double,
    val min: Double,
    val morn: Double,
    val night: Double
){
    fun toTemp(): Temp{
        return Temp(
            day = day,
            eve = eve,
            max = max,
            min = min,
            morn = morn,
            night = night
        )
    }
}