package com.android.example.weatherapp.data.local

import app.cash.turbine.test
import com.android.example.weatherapp.core.util.Resource
import com.android.example.weatherapp.core.util.WeatherUtils
import com.android.example.weatherapp.data.remote.WeatherApi
import com.android.example.weatherapp.data.testUtil.TestUtils
import com.android.example.weatherapp.domain.model.MAINZ
import com.android.example.weatherapp.domain.model.WeatherInfo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

    private var api: WeatherApi = mockk()
    private var dao: WeatherInfoDao = mockk()  // mockk(relaxed = true)
    private val weatherInfo: WeatherInfo = mockk()
    lateinit var tested: WeatherRepositoryImpl
    var weatherInfoResource: Resource<WeatherInfo> = mockk()

    @Before
    fun setUp() {
        tested = WeatherRepositoryImpl(api, dao)
    }

    @Test
    fun `WHEN fetching weather data Loading state is emitted first`() = runTest {

        val testGPS = MAINZ.gps

        tested.getWeatherInfo(
            testGPS.lat,
            testGPS.lon,
            WeatherUtils.EXCLUDE,
            WeatherUtils.UNITS,
            WeatherUtils.API_ID
        ).test {
            assertTrue(awaitItem() is Resource.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN weather data is stored in db THEN they are displayed while loading`() = runTest {

        val testGPS = MAINZ.gps

        coEvery {
            dao.getWeatherInfo(any(), any()).toWeatherInfo()
        } returns weatherInfo

        tested.getWeatherInfo(
            testGPS.lat,
            testGPS.lon,
            WeatherUtils.EXCLUDE,
            WeatherUtils.UNITS,
            WeatherUtils.API_ID
        ).test {
            awaitItem()
            assertEquals(awaitItem().data, weatherInfo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN fetching weather data, an error is emitted `() = runTest {

        val testGPS = MAINZ.gps

        coEvery {
            dao.getWeatherInfo(any(), any()).toWeatherInfo()
        } returns weatherInfo

        coEvery {
            api.getWeatherInfo(
                any(),
                any(),
                WeatherUtils.EXCLUDE,
                WeatherUtils.UNITS,
                WeatherUtils.API_ID
            )
        } throws TestUtils.newMockHttpException()

        tested.getWeatherInfo(
            testGPS.lat,
            testGPS.lon,
            WeatherUtils.EXCLUDE,
            WeatherUtils.UNITS,
            WeatherUtils.API_ID
        ).test {
            awaitItem()
            awaitItem()

            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertEquals(error.message, "Oops, something went wrong!")
            cancelAndIgnoreRemainingEvents()
        }
    }


    fun `WHEN fetching weather data, old data is `() {

    }
}