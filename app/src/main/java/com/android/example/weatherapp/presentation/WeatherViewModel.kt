package com.android.example.weatherapp.presentation

import android.content.Context
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.example.weatherapp.core.util.Gps
import com.android.example.weatherapp.core.util.WeatherUtils.getActivity
import com.android.example.weatherapp.domain.use_case.GetWeather
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.location.LocationServices
import com.plcoding.dictionary.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherInfo: GetWeather
) : ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val _gpsQuery = mutableStateOf(Gps(-1.0, -1.0))
    val gpsQuery: State<Gps> = _gpsQuery

    private val _state = mutableStateOf(WeatherInfoState())
    val state: State<WeatherInfoState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var weatherJob: Job? = null

    fun getWeatherWithCurrentLocation(context: Context) {

        fusedLocationClient = context.getActivity()
            ?.let { LocationServices.getFusedLocationProviderClient(it) }

        try{
            fusedLocationClient?.lastLocation
                ?.addOnSuccessListener { location : Location? ->
                    if(location?.latitude!= null){

                        val df = DecimalFormat("#.####")
                        df.roundingMode = RoundingMode.DOWN
                        val roundoffLat = df.format(location.latitude)
                        val roundoffLon = df.format(location.longitude)
                        getWeather(Gps(roundoffLat.toDouble(), roundoffLon.toDouble() ))
                    }

                }
        }catch(e:SecurityException){
            //TODO Snackbar
        }
    }

    fun isLocationCurrentlySelected(gps: Gps) : Boolean{
        if(_gpsQuery.value == gps){
            return true
        }
            return false
    }

    fun showGPSPermissionSnackBar(textToShow : String, buttonText: String, refused2ndTime: Boolean){
        viewModelScope.launch { _eventFlow.emit(
            UIEvent.ShowGPSPermissionSnackbar(textToShow,buttonText, refused2ndTime))}
    }

    fun getWeather(gps: Gps) {
        _gpsQuery.value = gps
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
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
                                weatherInfo = result.data,
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
                    }
                }.launchIn(this)
        }
    }
        sealed class UIEvent {
            data class ShowSnackbar(val message: String) : UIEvent()
            data class ShowGPSPermissionSnackbar(val message: String, val btnLabel:String, val refused2ndTime: Boolean ) : UIEvent()
        }

}