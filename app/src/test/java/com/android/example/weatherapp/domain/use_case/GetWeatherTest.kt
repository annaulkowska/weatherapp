package com.android.example.weatherapp.domain.use_case

import com.android.example.weatherapp.core.util.Resource
import com.android.example.weatherapp.core.util.WeatherUtils
import com.android.example.weatherapp.domain.model.MAINZ
import com.android.example.weatherapp.domain.model.WeatherInfo
import com.android.example.weatherapp.domain.repository.WeatherRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetWeatherTest {

    private var repository: WeatherRepository = mockk()
    lateinit var tested: GetWeather
    var weatherInfoResource: Resource<WeatherInfo> = mockk()

    @Before
    fun setUp() {
        tested = GetWeather(repository)
    }

    @Test
    fun `WHEN called with gps location (lat, lon) THEN weather info is returned`() {
        val testGPS = MAINZ.gps
        every {
            repository.getWeatherInfo(
                testGPS.lat,
                testGPS.lon,
                any(),
                any(),
                any()
            )
        } returns flowOf(weatherInfoResource)

        val result = tested.invoke(testGPS)

        verify {
            repository.getWeatherInfo(
                testGPS.lat,
                testGPS.lon,
                WeatherUtils.EXCLUDE,
                WeatherUtils.UNITS,
                WeatherUtils.API_ID
            )
        }

        runBlocking {
            assertThat(result.first()).isEqualTo(weatherInfoResource)
        }
    }
}