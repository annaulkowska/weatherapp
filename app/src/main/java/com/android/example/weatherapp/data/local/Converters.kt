package com.android.example.weatherapp.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.android.example.weatherapp.domain.model.Alert
import com.android.example.weatherapp.domain.model.Current
import com.android.example.weatherapp.domain.model.Daily
import com.google.gson.reflect.TypeToken
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
            object : TypeToken<Current>() {}.type
        )!!
    }

    @TypeConverter
    fun toCurrentJson(current: Current): String {
        return jsonParser.toJson(
            current,
            object : TypeToken<Current>(){}.type
        ) ?: "[]"
    }
}