package ru.itmo.weatherscreen.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.itmo.weatherscreen.data.DailyForecastEntity

@Database(entities = [DailyForecastEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dailyForecastDao(): DailyForecastDao
}
