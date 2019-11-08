package ru.itmo.weatherscreen

import android.app.Application
import androidx.room.Room
import ru.itmo.weatherscreen.db.AppDatabase
import ru.itmo.weatherscreen.weatherapi.WeatherApi
import ru.itmo.weatherscreen.weatherapi.createWeatherApi

class MyApp : Application() {
    lateinit var weatherApi: WeatherApi
        private set

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        val api = createWeatherApi()
        weatherApi = api

        database = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()

        app = this
    }

    companion object {
        lateinit var app: MyApp
            private set
    }
}
