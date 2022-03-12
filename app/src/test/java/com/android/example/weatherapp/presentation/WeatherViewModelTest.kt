package com.android.example.weatherapp.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.example.weatherapp.CoroutinesTestRule
import com.android.example.weatherapp.core.util.Resource
import com.android.example.weatherapp.core.util.WeatherUtils
import com.android.example.weatherapp.domain.model.WeatherInfo
import com.android.example.weatherapp.domain.use_case.GetWeather
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherViewModelTest {

    private var use_case: GetWeather = mockk()
    lateinit var tested: WeatherViewModel

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        tested = WeatherViewModel(use_case, coroutinesTestRule.testDispatcher)
    }

    @Test
    fun `WHEN getWeather() is called, THEN GetWeather usecase is called`() =
        runTest {
            val testGPS = WeatherUtils.MAINZ //pre cond
            every { use_case.invoke(any()) } returns mockk()
            tested.getWeather(testGPS) //test
            delay(100)
            coVerify { use_case.invoke(testGPS) } // asssert
        }

    @Test
    fun `WHEN getWeatherInfo(gps) is called and an error occures, return error`() {
        val testGPS = WeatherUtils.MAINZ
        val error: Resource.Error<WeatherInfo> = mockk()

        every { use_case.invoke(testGPS) } returns flowOf(error) //pre cond

        tested.getWeather(testGPS)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN getWeatherInfo(gps) is called and an error occures, show error snackbar`() = runTest {
        val testGPS = WeatherUtils.MAINZ
        val errorMessage = "Test error message"
        val error: Resource.Error<WeatherInfo> = mockk {
            every { data } returns mockk()
            every { message } returns errorMessage
        }
        every { use_case.invoke(testGPS) } returns flowOf(error) //pre cond
        val firstEvent = tested.eventFlow.first()

        tested.getWeather(testGPS)

        assertThat(firstEvent).isInstanceOf(WeatherViewModel.UIEvent.ShowSnackbar::class.java)
        assertThat((firstEvent as WeatherViewModel.UIEvent.ShowSnackbar).message).isEqualTo(
            errorMessage
        )
    }

    @Test
    fun `WHEN getWeatherInfo(gps) is called, THAN weather data is delivered`() {
        val testGPS = WeatherUtils.MAINZ
        val data: Resource.Success<WeatherInfo> = mockk()

        every { use_case.invoke(testGPS) } returns flowOf(data) //pre cond

        tested.getWeather(testGPS)
    }

    @Test
    fun `WHEN location is currently selected, THAN return true`() {
        val testGPS = WeatherUtils.MAINZ
        val result = tested.isLocationCurrentlySelected(testGPS)
        assertThat(result).isTrue()
    }


    @Test
    fun `WHEN location is currently NOT selected, THAN return false`() {
        val testGPS = WeatherUtils.DARMSTADT
        val result = tested.isLocationCurrentlySelected(testGPS)
        assertThat(result).isFalse()
    }
}