package com.android.example.weatherapp.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.example.weatherapp.CoroutinesTestRule
import com.android.example.weatherapp.core.util.Resource
import com.android.example.weatherapp.domain.model.MAINZ
import com.android.example.weatherapp.domain.model.WIESBADEN
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
import kotlinx.coroutines.test.advanceUntilIdle
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN getWeather() is called, THEN GetWeather usecase is called`() =
        runTest {
            val testGPS = MAINZ.gps //pre cond
            every { use_case.invoke(any()) } returns mockk()
            tested.getWeather(testGPS) //test
            delay(100)
            coVerify { use_case.invoke(testGPS) } // assert
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN getWeatherInfo(gps) is called, THAN weather data is delivered`() = runTest {
        val testGPS = MAINZ.gps
        val weatherInfo: WeatherInfo = mockk()
        val success: Resource.Success<WeatherInfo> = mockk {
            every { data } returns weatherInfo
        }
        every { use_case.invoke(testGPS) } returns flowOf(success) //pre cond

        tested.getWeather(testGPS)
        advanceUntilIdle()

        assertThat(tested.state.value.weatherInfo).isEqualTo(weatherInfo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN getWeatherInfo(gps) is called and an error occurs, show error snackbar`() = runTest {
        val testGPS = MAINZ.gps
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
    fun `WHEN city is selected, THAN update WeatherInfoState`() {
        val testCity = WIESBADEN

        tested.onCitySelected(testCity)
        val updatedWeatherState: WeatherInfoState = tested.state.value

        assertThat(updatedWeatherState.cityBtnList.find { it.city == testCity }!!.isSelected).isTrue()

    }
}