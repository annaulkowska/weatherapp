package com.android.example.weatherapp.presentation

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.android.example.weatherapp.core.util.WeatherUtils
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

    WeatherAppTheme {
        val permissionState = rememberPermissionState(
            permission = Manifest.permission.ACCESS_FINE_LOCATION
        )
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {

                FloatingActionButton(onClick = {

                    // Test()
                    val gps = viewModel.getGpsLocation()
                    viewModel.getWeather(gps)
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
                    .background(MaterialTheme.colors.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {

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

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
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


    @Composable
    fun PermissionInformation() {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Location permission accepted")
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

        } else {

        }

    }

    @ExperimentalPermissionsApi
    @Composable
    fun RequireLocationPermission(
        navigateToSettingsScreen: () -> Unit,
        content: @Composable() () -> Unit
    ) {
        // Track if the user doesn't want to see the rationale any more.
        var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

        // Permission state
        val permissionState = rememberMultiplePermissionsState(
            listOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        when {
            permissionState.allPermissionsGranted -> {
                content()
            }
            // If the user denied the permission but a rationale should be shown, or the user sees
            // the permission for the first time, explain why the feature is needed by the app and allow
            // the user to be presented with the permission again or to not see the rationale any more.

            //  || !permissionState.permissionRequested
            permissionState.shouldShowRationale -> {
                if (doNotShowRationale) {
                    Text("Feature not available")
                } else {
                    Column {
                        Text("Need to detect current location. Please grant the permission.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                Text("Request permission")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { doNotShowRationale = true }) {
                                Text("Don't show rationale again")
                            }
                        }
                    }
                }
            }
            // If the criteria above hasn't been met, the user denied the permission. Let's present
            // the user with a FAQ in case they want to know more and send them to the Settings screen
            // to enable it the future there if they want to.
            else -> {
                Column {
                    Text(
                        "Request location permission denied. " +
                                "Need current location to show nearby places. " +
                                "Please grant access on the Settings screen."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = navigateToSettingsScreen) {
                        Text("Open Settings")
                    }
                }
            }
        }
    }

    @ExperimentalPermissionsApi
    @Composable
    fun Test() {
        val context = LocalContext.current

        RequireLocationPermission(navigateToSettingsScreen = {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
            )
        }) {
            Text("Location Permission Accessible")
        }
    }


}