package com.android.example.weatherapp.di

import android.app.Application
import androidx.room.Room
import com.android.example.weatherapp.data.local.Converters
import com.android.example.weatherapp.data.local.WeatherInfoDatabase
import com.android.example.weatherapp.data.local.WeatherRepositoryImpl
import com.android.example.weatherapp.data.remote.WeatherApi
import com.android.example.weatherapp.domain.repository.WeatherRepository
import com.google.gson.Gson
import com.plcoding.dictionary.feature_dictionary.data.util.GsonParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(app: Application): WeatherInfoDatabase {
        return Room.databaseBuilder(
            app,
            WeatherInfoDatabase::class.java,
            "weather_db"
        ).addTypeConverter(Converters(GsonParser(Gson()))).build()
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(db: WeatherInfoDatabase, api: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(api, db.dao)
    }
}