package ru.itmo.weatherscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.weather.*
import retrofit2.Call
import ru.itmo.weatherscreen.data.Weather
import retrofit2.Callback
import retrofit2.Response
import ru.itmo.weatherscreen.data.DailyForecast
import ru.itmo.weatherscreen.data.DailyForecastEntity
import ru.itmo.weatherscreen.db.AppDatabase
import ru.itmo.weatherscreen.db.DailyForecastDao
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import ru.itmo.weatherscreen.intentservice.AppIntentService


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TODAY_INDEX = 0
        private const val TEMP_FORMAT = "%.1f°C"
        private const val SPB_LOCATION_CODE = "295212"
    }

    private val LOG_TAG = "Weather api"
    private var dailyForecasts: ArrayList<DailyForecastEntity>? = ArrayList()


    private lateinit var receiver: AppReceiver

    private var call: Call<Weather>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            run()
        }
    }

    override fun onStart() {
        setReceiver()
        super.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("dailyForecasts", dailyForecasts)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        dailyForecasts = savedInstanceState.getParcelableArrayList("dailyForecasts")
        setData()
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
        call = null
    }

    override fun onStop() {
        Log.d(LOG_TAG, "onStop..")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onStop()
    }

    private fun setReceiver() {
        Log.i(LOG_TAG, "Setting receiver..")

        receiver = AppReceiver()

        val intentFilter = IntentFilter().apply {
            addAction(getString(R.string.filter_key))
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    private fun run() {

        val intent = Intent().apply {
            setClass(this@MainActivity, AppIntentService::class.java)
            intent.putExtra("get", true)
        }
        startService(intent)

        call = MyApp.app.weatherApi.getDailyForecasts(
            SPB_LOCATION_CODE,
            BuildConfig.API_KEY,
            "ru-RU",
            true
        )

        call?.enqueue(object : Callback<Weather> {
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Log.e(LOG_TAG, "failed with ${t.localizedMessage} ", t)
            }

            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                val body = response.body()

                Log.d(LOG_TAG, "Finished with ${response.code()}, body: $body")

                if (body != null) {
                    intent.putExtra("get", false)

                    var id = 0
                    dailyForecasts = ArrayList()
                    body.dailyForecastList.forEach {
                        dailyForecasts!!.add(createAndGetDailyForecastEntity(id++, it))
                    }
                    setData()

                    intent.putParcelableArrayListExtra("dataToInsert", dailyForecasts)
                    startService(intent)
                }

            }
        })

    }

    private fun setData() {
        if (switcher.currentView == progress_bar) {
            switcher.showNext()
        }

        val daysAmount = dailyForecasts?.size ?: 0

        assert(daysAmount > 0)

        todayWeather(dailyForecasts?.get(TODAY_INDEX))

        for (i in 0 until daysAmount) {
            val curForecast = dailyForecasts!![i]

            val dayId = getTypeId("day_${i + 1}", "id")
            findViewById<TextView>(dayId)?.text = curForecast.dayOfWeek

            val descriptionId = getTypeId("description_${i + 1}", "id")
            findViewById<TextView>(descriptionId)?.text = curForecast.tempr


            val imgId = getTypeId("img_${i + 1}", "id")
            val imgResourceId =
                getTypeId(curForecast.iconName, "drawable")
            findViewById<ImageView>(imgId)?.setImageResource(imgResourceId)
        }
    }

    private fun todayWeather(dailyForecast: DailyForecastEntity?) {
        if (dailyForecast === null) {
            Log.e(LOG_TAG, "Can't set today weather cause of null")
            return
        }

        val imgResourceId = getTypeId(dailyForecast.iconName, "drawable")
        main_weather_image.setImageResource(imgResourceId)
        weather_description.text = dailyForecast.weatherDescr
        main_temperature.text = dailyForecast.tempr
    }

    private fun getTypeId(name: String, defType: String): Int {
        return resources.getIdentifier(name, defType, packageName)
    }

    private fun createAndGetDailyForecastEntity(
        id: Int,
        dailyForecast: DailyForecast
    ): DailyForecastEntity {
        return DailyForecastEntity(
            id,
            TEMP_FORMAT.format(dailyForecast.temperature.temperature),
            SimpleDateFormat(
                "EE",
                Locale("ru")
            ).format(dailyForecast.date),
            "ic_${dailyForecast.pictureInfo.iconNumber}",
            dailyForecast.pictureInfo.iconPhrase
        )
    }

    private inner class AppReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getBooleanExtra("get", true)!!) {
                dailyForecasts = intent.getParcelableArrayListExtra("dailyForecast")
                setData()
            }
        }

    }

}