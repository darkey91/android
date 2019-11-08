package ru.itmo.weatherscreen.db

import androidx.room.*
import ru.itmo.weatherscreen.data.DailyForecastEntity

@Dao
interface DailyForecastDao {

    companion object {
        private const val TABLE_NAME ="dailyForecastEntity";
    }

    @Query("SELECT * FROM $TABLE_NAME ORDER BY id asc")
    fun getAll(): List<DailyForecastEntity>?

    @Query("SELECT * FROM $TABLE_NAME WHERE id in (:ids)")
    fun getForecastsByIds(ids: ArrayList<Int>): List<DailyForecastEntity>?

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getForecastById(id: Int): DailyForecastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dailyForecastEntity: DailyForecastEntity)

    @Update
    fun update(dailyForecastEntity: DailyForecastEntity)
}