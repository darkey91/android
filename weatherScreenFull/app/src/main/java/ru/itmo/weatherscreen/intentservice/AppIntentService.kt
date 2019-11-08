package ru.itmo.weatherscreen.intentservice

import android.app.IntentService
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.itmo.weatherscreen.MyApp
import ru.itmo.weatherscreen.R
import ru.itmo.weatherscreen.data.DailyForecastEntity
import ru.itmo.weatherscreen.db.AppDatabase
import ru.itmo.weatherscreen.db.DailyForecastDao
import java.util.ArrayList

class AppIntentService(name: String = "") : IntentService(name) {
    private val LOG_TAG = "AppIntentService"

    private var db: AppDatabase? = MyApp.app.database
    private var dao: DailyForecastDao? = db!!.dailyForecastDao()

    override fun onHandleIntent(intent: Intent?) {
        Log.i(LOG_TAG, "Handling..")

        if (intent == null) {
            Log.e(LOG_TAG, "onHandleIntent: intent == null")
            return
        }

        intent.action = getString(R.string.filter_key)

        if (intent.getBooleanExtra("get", true)) {
            LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(
                    intent.putParcelableArrayListExtra(
                        "dailyForecast",
                        dao!!.getAll() as ArrayList<out Parcelable>
                    )
                )
        } else {
            intent.getParcelableArrayListExtra<DailyForecastEntity>("dataToInsert")?.forEach {
                dao!!.insert(it)
            }
        }

    }


}