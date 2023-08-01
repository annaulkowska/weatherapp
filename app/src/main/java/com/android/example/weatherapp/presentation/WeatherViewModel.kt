package com.android.example.weatherapp.presentation

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.example.weatherapp.core.util.DispatcherProvider
import com.android.example.weatherapp.core.util.Resource
import com.android.example.weatherapp.core.util.WeatherUtils.getActivity
import com.android.example.weatherapp.domain.model.City
import com.android.example.weatherapp.domain.model.CityBtnModel
import com.android.example.weatherapp.domain.model.DARMSTADT
import com.android.example.weatherapp.domain.model.FRANKFURT_AM_MAIN
import com.android.example.weatherapp.domain.model.Gps
import com.android.example.weatherapp.domain.model.MAINZ
import com.android.example.weatherapp.domain.model.WIESBADEN
import com.android.example.weatherapp.domain.use_case.GetWeather
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherInfo: GetWeather,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val _state = MutableStateFlow(WeatherInfoState())
    val state: StateFlow<WeatherInfoState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var weatherJob: Job? = null
    init {
        _state.value = _state.value.copy(
            cityBtnList = listOf(
                CityBtnModel(MAINZ, true),
                CityBtnModel(FRANKFURT_AM_MAIN, false),
                CityBtnModel(WIESBADEN, false),
                CityBtnModel(DARMSTADT, false),
            )
        )
        onCitySelected(MAINZ)
    }
    fun getWeatherWithCurrentLocation(context: Context) {
        fusedLocationClient = context.getActivity()
            ?.let { LocationServices.getFusedLocationProviderClient(it) }
        try {
            fusedLocationClient?.lastLocation
                ?.addOnSuccessListener { location: Location? ->
                    if (location?.latitude != null) {
                        getWeather(formatGpsCoordinates(location))
                        updateCitySelection(null)
                    }
                }
        } catch (e: SecurityException) {
            Log.e(
                WeatherViewModel::class.java.simpleName,
                "Failed to acquire weather for current location ",
                e
            )
        }
    }
    private fun formatGpsCoordinates(location: Location): Gps {
        val df = DecimalFormat("#.####")
        df.roundingMode = RoundingMode.DOWN
        val roundoffLat = df.format(location.latitude)
        val roundoffLon = df.format(location.longitude)
        return Gps(roundoffLat.toDouble(), roundoffLon.toDouble())
    }

    fun showGPSPermissionSnackBar(textToShow: String, buttonText: String, refused2ndTime: Boolean) {
        viewModelScope.launch {
            _eventFlow.emit(
                UIEvent.ShowGPSPermissionSnackbar(textToShow, buttonText, refused2ndTime)
            )
        }
    }
    fun getWeather(gps: Gps) {
        //   _gpsQuery.value = gps
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch(dispatchers.main) {
            getWeatherInfo(gps)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                weatherInfo = result.data,
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                weatherInfo = result.data, // null
                                isLoading = false
                            )
                            _eventFlow.emit(
                                UIEvent.ShowSnackbar(
                                    result.message ?: "Unknown error"
                                )
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                weatherInfo = result.data,
                                isLoading = true
                            )
                        }

                        else -> {}
                    }
                }.launchIn(this)
        }
    }

    fun onCitySelected(city: City) {
//        Log.d("###", "onCitySelected: $city")
        getWeather(city.gps)
        updateCitySelection(city)
    }

    private fun updateCitySelection(city: City?) {
        val updatedBtnList = state.value.cityBtnList.map { cityBtnModel ->
            cityBtnModel.copy(
                isSelected = cityBtnModel.city == city
            )
        }
        _state.update { weatherInfoState ->
            weatherInfoState.copy(
                cityBtnList = updatedBtnList
            )
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
        data class ShowGPSPermissionSnackbar(
            val message: String,
            val btnLabel: String,
            val refused2ndTime: Boolean
        ) : UIEvent()
    }
}

