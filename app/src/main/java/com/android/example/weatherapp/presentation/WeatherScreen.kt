package com.android.example.weatherapp.presentation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.preference.PreferenceManager
import coil.compose.rememberAsyncImagePainter
import com.android.example.weatherapp.R
import com.android.example.weatherapp.core.util.WeatherUtils
import com.android.example.weatherapp.domain.model.City
import com.android.example.weatherapp.domain.model.Current
import com.android.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@ExperimentalPermissionsApi
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val weatherState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = LocalContext.current.resources

    WeatherAppTheme {
        val locationPermissionsState = rememberMultiplePermissionsState(
            listOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
        LaunchedEffect(key1 = true) {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is WeatherViewModel.UIEvent.ShowSnackbar -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = event.message, duration = SnackbarDuration.Short
                        )
                    }

                    is WeatherViewModel.UIEvent.ShowGPSPermissionSnackbar -> {
                        val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = event.btnLabel,
                            duration = SnackbarDuration.Long
                        )
                        when (snackbarResult) {
                            SnackbarResult.Dismissed -> Log.d("Snackbar", "Dismissed")
                            SnackbarResult.ActionPerformed -> {
                                if (event.refused2ndTime) {
                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", context.packageName, null)
                                        )
                                    )
                                } else {
                                    locationPermissionsState.launchMultiplePermissionRequest()
                                    PreferenceManager.getDefaultSharedPreferences(context).edit()
                                        .putBoolean(WeatherUtils.KEY_PERMISSION_REQUESTED, true)
                                        .apply()
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
        Scaffold(scaffoldState = scaffoldState, floatingActionButton = {
            FloatingActionButton(onClick = {
                if (locationPermissionsState.allPermissionsGranted) {
                    viewModel.getWeatherWithCurrentLocation(context)
                } else {
                    val allPermissionsRevoked =
                        locationPermissionsState.permissions.size == locationPermissionsState.revokedPermissions.size
                    var refused2time = false
                    val textToShow = if (locationPermissionsState.shouldShowRationale) {
                        resources.getString(R.string.location_is_important)
                    } else {
                        resources.getString(R.string.this_feature_requires_location_permission)
                    }
                    if (!locationPermissionsState.shouldShowRationale && PreferenceManager.getDefaultSharedPreferences(
                            context
                        ).getBoolean(WeatherUtils.KEY_PERMISSION_REQUESTED, false)
                    ) {
                        refused2time = true
                    }
                    val buttonText = if (!allPermissionsRevoked) {
                        resources.getString(R.string.allow_precise_location)
                    } else if (refused2time) {
                        resources.getString(R.string.open_app_settings)
                    } else {
                        resources.getString(R.string.request_permissions)
                    }
                    viewModel.showGPSPermissionSnackBar(textToShow, buttonText, refused2time)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.LocationSearching,
                    contentDescription = "SearchWeather"
                )
            }
        }) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colors.background)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            FlowRow(
                                Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(
                                    16.dp,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                weatherState.cityBtnList.forEach {
                                    CityButton(
                                        city = it.city,
                                        isSelected = it.isSelected,
                                        onCityClicked = { viewModel.onCitySelected(it.city) }
                                    )
                                }
                            }
                            weatherState.weatherInfo?.current?.let {
                                WeatherToday(currentWeather = it)
                            }
                        }
                        weatherState.weatherInfo?.dailys?.let { dailys ->
                            items(dailys.size) { i ->
                                val daily = weatherState.weatherInfo!!.dailys[i]
                                if (i > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                DailyItem(daily = daily, weatherState.weatherInfo!!.timezone_offset)
                                if (i < weatherState.weatherInfo!!.dailys.size - 1) {
                                    Divider()
                                }
                            }
                        }
                    }
                }
                if (weatherState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun CityButton(
    city: City,
    isSelected: Boolean,
    onCityClicked: () -> Unit
) {
    Button(
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        } else {
            ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        },
        onClick = { onCityClicked() },
    ) {
        Text(city.cityName)
    }
}

@Composable
fun WeatherToday(
    currentWeather: Current
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(
                R.string.wind_speed,
                currentWeather.wind_speed.toString() ?: ""
            ), style = MaterialTheme.typography.h6
        )
        Text(
            text = currentWeather.weather.get(0).description ?: "",
            style = MaterialTheme.typography.h6
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(
                R.string.pressure,
                currentWeather.pressure.toString() ?: ""
            ), style = MaterialTheme.typography.h6
        )
        Text(
            text = stringResource(
                R.string.humidity,
                currentWeather.humidity?.toString() ?: ""
            ), style = MaterialTheme.typography.h6
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(
                R.string.dew_point,
                currentWeather.dew_point.roundToInt().toString()
            ), style = MaterialTheme.typography.h6
        )
        Text(
            text = stringResource(
                R.string.visibility,
                currentWeather.visibility.toString() ?: ""
            ), style = MaterialTheme.typography.h6
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentWeather.temp.roundToInt().toString() + "°C",
            style = MaterialTheme.typography.h4
        )
        Image(
            painter = rememberAsyncImagePainter(
                "https://openweathermap.org/img/wn/${
                    currentWeather.weather.get(
                        0
                    )?.icon
                }@2x.png"
            ), contentDescription = "current_weather_image", modifier = Modifier.size(88.dp)
        )
        Text(
            text = stringResource(
                R.string.feels_like,
                currentWeather.feels_like.roundToInt().toString()
            ), style = MaterialTheme.typography.h5
        )
    }
}