package com.android.example.weatherapp.data.remote.dto

import com.android.example.weatherapp.domain.model.Alert

data class AlertDto(
    val description: String,
    val end: Int,
    val event: String,
    val sender_name: String,
    val start: Int,
    val tags: List<String>
){
    fun toAlert(): Alert {
        return Alert(
            description = description,
            end = end,
            event = event,
            sender_name = sender_name,
            start = start,
            tags = tags
        )
    }
}