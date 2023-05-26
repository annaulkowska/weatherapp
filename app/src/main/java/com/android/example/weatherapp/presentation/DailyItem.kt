package com.android.example.weatherapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.example.weatherapp.domain.model.Daily
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyItem(
    daily: Daily,
    offset: Int
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text =  formatDate(daily.dt, offset/3600),
            fontSize = 16.sp
        )
        Text(
            text = daily.temp.max.roundToInt().toString() + "/" + daily.temp.min.roundToInt()
                .toString() + "°C",
            fontSize = 16.sp,

            )
        Image(
            painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${daily.weather.get(0).icon}@2x.png"),
            contentDescription = "current_weather_image",
            modifier = Modifier.size(44.dp)
        )
    }
}
fun formatDate(time: Int, offset: Int, format: String = "EEE, MMMM d"): String {
    // parse the time zone
    val zoneOffset = ZoneOffset.ofHours(offset)
    // create a moment in time from the given timestamp (in seconds!)
    val instant = Instant.ofEpochSecond(time.toLong())
    // define a formatter using the given pattern and a Locale
    val formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
    // then make the moment in time consider the zone and return the formatted String
    return instant.atOffset(zoneOffset).format(formatter)
}