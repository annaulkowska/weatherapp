package com.android.example.weatherapp.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.example.weatherapp.core.util.Gps
import com.android.example.weatherapp.domain.model.WeatherInfo
import com.android.example.weatherapp.domain.use_case.GetWeather
import com.plcoding.dictionary.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherInfo: GetWeather
) : ViewModel() {


    private val _gpsQuery = mutableStateOf(Gps(-1.0, -1.0))
    val gpsQuery: State<Gps> = _gpsQuery

    private val _state = mutableStateOf(WeatherInfoState())
    val state: State<WeatherInfoState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var weatherJob: Job? = null

    fun getGpsLocation(): Gps {

        return Gps(-1.0, -1.0)
    }

    fun isLocationCurrentlySelected(gps: Gps) : Boolean{
        if(_gpsQuery.value == gps){
            return true
        }
            return false
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
        }

}