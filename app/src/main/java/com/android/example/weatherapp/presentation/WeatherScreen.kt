package com.android.example.weatherapp.presentation

import android.content.Intent
import android.net.Uri
import androidx.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.example.weatherapp.core.util.WeatherUtils
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherAppTheme {

    }
}

@ExperimentalPermissionsApi
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.state.value
    val context = LocalContext.current

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
                            message = event.message, duration = SnackbarDuration.Long
                        )
                    }
                    is WeatherViewModel.UIEvent.ShowGPSPermissionSnackbar -> {
                        val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = event.btnLabel, duration = SnackbarDuration.Long
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
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(WeatherUtils.KEY_PERMISSION_REQUESTED, true).apply()
                                }
                            }
                        }
                    }
                }
            }
        }

        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {

                FloatingActionButton(onClick = {

                    if (locationPermissionsState.allPermissionsGranted) {
                        viewModel.getWeatherWithCurrentLocation(context)
                    } else {
                        val allPermissionsRevoked =
                            locationPermissionsState.permissions.size ==
                                    locationPermissionsState.revokedPermissions.size

                        var refused2time = false
                        val textToShow =  if (locationPermissionsState.shouldShowRationale) {
                            "Getting your location is important for if you want to check the weather for your place. Thank you :D"
                        } else {
                            "This feature requires location permission"
                        }
                        if (!locationPermissionsState.shouldShowRationale && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(WeatherUtils.KEY_PERMISSION_REQUESTED, false)) {
                            refused2time = true
                        }

                        val buttonText = if (!allPermissionsRevoked) {
                            "Allow precise location"
                        } else if (refused2time) {
                            "Open App Settings"
                        } else {
                            "Request permissions"
                        }
                        viewModel.showGPSPermissionSnackBar(textToShow, buttonText, refused2time)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.LocationSearching,
                        contentDescription = "SearchWeather"
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colors.background)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            LocationButtons(viewModel)
                        }
                        state.weatherInfo?.dailys?.let { dailys ->
                            items(dailys.size) { i ->
                                val daily = state.weatherInfo.dailys[i]
                                if (i > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                DailyItem(daily = daily, state.weatherInfo.timezone_offset)
                                if (i < state.weatherInfo.dailys.size - 1) {
                                    Divider()
                                }
                            }
                        }
                    }

                }
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

    }
}

@Composable
fun LocationButtons(viewModel: WeatherViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(
            colors = if (viewModel.isLocationCurrentlySelected(WeatherUtils.MAINZ)) {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            } else {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            },
            onClick = {
                viewModel.getWeather(WeatherUtils.MAINZ)
            },
        ) {
            Text("MAINZ")
        }
        Button(
            colors = if (viewModel.isLocationCurrentlySelected(WeatherUtils.DARMSTADT)) {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            } else {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            },
            onClick = {
                viewModel.getWeather(WeatherUtils.DARMSTADT)
            },
        ) {
            Text("DARMSTADT")
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(
            colors = if (viewModel.isLocationCurrentlySelected(WeatherUtils.WIESBADEN)) {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            } else {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            },
            onClick = {
                viewModel.getWeather(WeatherUtils.WIESBADEN)
            },
        ) {
            Text("WIESBADEN")
        }
        Button(
            colors = if (viewModel.isLocationCurrentlySelected(WeatherUtils.FRANKFURT_AM_MAIN)) {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            } else {
                ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            },
            onClick = {
                viewModel.getWeather(WeatherUtils.FRANKFURT_AM_MAIN)
            },
        ) {
            Text("FRANKFURT AM MAIN")
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Wind speed: " + viewModel.state.value.weatherInfo?.current?.wind_speed?.toString() + " m/s ",
            fontSize = 16.sp
        )
        Text(
            text = viewModel.state.value.weatherInfo?.current?.weather?.get(0)?.description
                ?: "", fontSize = 16.sp
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Pressure: " + viewModel.state.value.weatherInfo?.current?.pressure?.toString() + " hPA",
            fontSize = 16.sp
        )
        Text(
            text = "Humidity: " + viewModel.state.value.weatherInfo?.current?.humidity?.toString() + " %",
            fontSize = 16.sp
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Dew point: " + viewModel.state.value.weatherInfo?.current?.dew_point?.roundToInt()
                .toString() + " °C",
            fontSize = 16.sp
        )
        Text(
            text = "Visibility: " + viewModel.state.value.weatherInfo?.current?.visibility?.toString() + " km",
            fontSize = 16.sp
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = viewModel.state.value.weatherInfo?.current?.temp?.roundToInt()
                .toString() + "°C",
            fontSize = 30.sp
        )
        Image(
            painter = rememberAsyncImagePainter(
                "https://openweathermap.org/img/wn/${
                    viewModel.state.value.weatherInfo?.current?.weather?.get(
                        0
                    )?.icon
                }@2x.png"
            ),
            contentDescription = "current_weather_image",
            modifier = Modifier.size(88.dp)
        )
        Text(
            text = "Feels like: " + viewModel.state.value.weatherInfo?.current?.feels_like?.roundToInt()
                .toString() + "°C",
            fontSize = 24.sp
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

