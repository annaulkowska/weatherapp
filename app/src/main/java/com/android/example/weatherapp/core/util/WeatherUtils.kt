package com.android.example.weatherapp.core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.android.example.weatherapp.domain.model.Gps

object WeatherUtils {
    val MAINZ = Gps(50.0, 8.2711)
    val DARMSTADT = Gps(49.8706, 8.6494)
    val WIESBADEN = Gps(50.0833, 8.25)
    val FRANKFURT_AM_MAIN = Gps(50.1167, 8.6833)
    const val EXCLUDE = "hourly,minutely"
    const val API_ID = "60fd3193b8b8cc5e06be0b02cbb690ba"
    const val UNITS = "metric"
    const val KEY_PERMISSION_REQUESTED = "permissionRequested"

    fun Context.getActivity(): Activity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }
}