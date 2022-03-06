package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.domain.model.FeelsLike

data class FeelsLikeDto(
    val day: Double,
    val eve: Double,
    val morn: Double,
    val night: Double
){
    fun toFeelsLike(): FeelsLike{
        return FeelsLike(
            day = day,
            eve = eve,
            morn = morn,
            night = night
        )
    }
}