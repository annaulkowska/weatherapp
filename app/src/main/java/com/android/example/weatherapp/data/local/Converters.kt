package com.android.example.weatherapp.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.android.example.weatherapp.domain.model.*
import com.google.gson.reflect.TypeToken
import com.plcoding.dictionary.feature_dictionary.data.util.GsonParser
import com.plcoding.dictionary.feature_dictionary.data.util.JsonParser


@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {
    @TypeConverter
    fun fromAlertsJson(json: String): List<Alert> {
        return jsonParser.fromJson<ArrayList<Alert>>(
            json,
            object : TypeToken<ArrayList<Alert>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toAlertsJson(alerts: List<Alert>): String {
        return jsonParser.toJson(
            alerts,
            object : TypeToken<ArrayList<Alert>>(){}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromDailysJson(json: String): List<Daily> {
        return jsonParser.fromJson<ArrayList<Daily>>(
            json,
            object : TypeToken<ArrayList<Daily>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toDailysJson(dailys: List<Daily>): String {
        return jsonParser.toJson(
            dailys,
            object : TypeToken<ArrayList<Daily>>(){}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromCurrentJson(json: String): Current {
        return jsonParser.fromJson<Current>(
            json,
            object : TypeToken<Current>(){}.type
        )!! //TODO
    }

    @TypeConverter
    fun toCurrentJson(current: Current): String {
        return jsonParser.toJson(
            current,
            object : TypeToken<Current>(){}.type
        ) ?: "[]"
    }


    @TypeConverter
    fun fromWeatherJson(json: String): List<Weather> {
        return jsonParser.fromJson<ArrayList<Weather>>(
            json,
            object : TypeToken<ArrayList<Weather>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toWeatherJson(weatherlist: List<Weather>): String {
        return jsonParser.toJson(
            weatherlist,
            object : TypeToken<ArrayList<Weather>>(){}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromWeatherXJson(json: String): List<WeatherX> {
        return jsonParser.fromJson<ArrayList<WeatherX>>(
            json,
            object : TypeToken<ArrayList<WeatherX>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toWeatherXJson(weatherlist: List<WeatherX>): String {
        return jsonParser.toJson(
            weatherlist,
            object : TypeToken<ArrayList<WeatherX>>(){}.type
        ) ?: "[]"
    }
}