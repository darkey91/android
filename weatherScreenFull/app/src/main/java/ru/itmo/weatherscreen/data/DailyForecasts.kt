package ru.itmo.weatherscreen.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

data class Weather(
    @Json(name = "DailyForecasts") val dailyForecastList: List<DailyForecast>
)

data class DailyForecast(
    @Json(name = "Date") val date: Date,
    @Json(name = "Temperature") val temperature: Temperature,
    @Json(name = "Day") val pictureInfo: PictureInfo
)

data class Temperature(
    @Json(name = "Minimum") val minimum: TemperatureValue,
    @Json(name = "Maximum") val maximum: TemperatureValue
) {
    val temperature = (minimum.value + maximum.value) / 2
}

data class PictureInfo(
    @Json(name = "Icon") val iconNumber: Int,
    @Json(name = "IconPhrase") val iconPhrase: String
) {
    val iconName = "ic_${iconNumber}"
}

data class TemperatureValue(
    @Json(name = "Value") val value: Double,
    @Json(name = "Unit") val unit: String
)


@Parcelize
@Entity(tableName = "dailyForecastEntity")
data class DailyForecastEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "temperature") val tempr: String,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: String,
    @ColumnInfo(name = "icon_name") val iconName: String,
    @ColumnInfo(name = "weather_description") val weatherDescr: String
) : Parcelable