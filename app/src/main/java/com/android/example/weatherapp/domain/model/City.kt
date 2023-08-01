package com.android.example.weatherapp.domain.model

data class City(val cityName: String, val gps: Gps)

val MAINZ = City("MAINZ", Gps(50.0, 8.2711))
val DARMSTADT = City("DARMSTADT", Gps(49.8706, 8.6494))
val WIESBADEN = City("WIESBADEN", Gps(50.0833, 8.25))
val FRANKFURT_AM_MAIN = City("FRANKFURT AM MAIN", Gps(50.1167, 8.6833))
