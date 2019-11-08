package ru.itmo.weatherscreen.weatherapi

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.itmo.weatherscreen.BuildConfig
import ru.itmo.weatherscreen.data.Weather
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

interface WeatherApi {
    @GET("forecasts/v1/daily/5day/{locationCode}")
    fun getDailyForecasts(@Path("locationCode") locationCode: String,
                          @Query("apikey") apiKey: String,
                          @Query("language") lang: String,
                          @Query("metric") metric: Boolean): Call<Weather>
}

abstract class NetworkConnectionInterceptor: Interceptor {
    abstract fun isInternetAvailable(): Boolean

    abstract fun onInternetUnavailable()

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response? {
        val request: Request = chain.request()
        if (!isInternetAvailable()) {
            onInternetUnavailable()
        }
        return chain.proceed(request)
    }
}


fun createWeatherApi(): WeatherApi {
    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("https://dataservice.accuweather.com/")
        .build()

    val api = retrofit.create(WeatherApi::class.java)
    return api
}