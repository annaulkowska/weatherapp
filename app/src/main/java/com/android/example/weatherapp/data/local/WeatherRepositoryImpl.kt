package com.android.example.weatherapp.data.local

import com.android.example.weatherapp.core.util.WeatherUtils
import com.android.example.weatherapp.data.remote.WeatherApi
import com.android.example.weatherapp.domain.model.WeatherInfo
import com.android.example.weatherapp.domain.repository.WeatherRepository
import com.plcoding.dictionary.core.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImpl(
    private val api: WeatherApi,
    private val dao: WeatherInfoDao
): WeatherRepository {

    override fun getWeatherInfo(
        lat: Double,
        lon: Double,
        exclude: String,
        units : String,
        appid: String
    ): Flow<Resource<WeatherInfo>> = flow {

        emit(Resource.Loading())
        val weatherInfo = dao.getWeatherInfo(lat, lon)?.toWeatherInfo()
        emit(Resource.Loading(data = weatherInfo))

        try {
            val remoteWeatherInfo = api.getWeatherInfo(lat, lon, WeatherUtils.EXCLUDE, WeatherUtils.UNITS, WeatherUtils.API_ID)
            dao.deleteWeatherInfo(remoteWeatherInfo.lat, remoteWeatherInfo.lon )
            dao.insertWeatherInfos(remoteWeatherInfo.toWeatherInfoEntity())
        } catch(e: HttpException) {
            emit(Resource.Error(
                message = "Oops, something went wrong!",
                data = weatherInfo
            ))
        } catch(e: IOException) {
            emit(Resource.Error(
                message = "Couldn't reach server, check your internet connection.",
                data = weatherInfo
            ))
        }

        val newWeatherInfo = dao.getWeatherInfo(lat, lon).toWeatherInfo()
        emit(Resource.Success(newWeatherInfo))
    }
}