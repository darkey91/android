package ru.itmo.pictureviewer.asynctask

import android.app.IntentService
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import ru.itmo.pictureviewer.BuildConfig
import ru.itmo.pictureviewer.ImageActivity
import ru.itmo.pictureviewer.MainActivity
import java.net.URL
import ru.itmo.pictureviewer.R
import ru.itmo.pictureviewer.data.Image
import ru.itmo.pictureviewer.utils.ImageFetcher

private var pagesAmount = 1


class ImageLoaderIntentService(name: String = "") : IntentService(name) {
    private val LOG_TAG = "ImageLoaderIntentServic"

    private val perPage = 10

    private val BASE_API_URL: String = "https://api.unsplash.com/"

    private val ACCESSIBLE_API_URL: String =
        "${BASE_API_URL}photos/?page=${pagesAmount}&per_page=${perPage}&client_id=${BuildConfig.API_KEY}"


    override fun onHandleIntent(intent: Intent?) {
        Log.i(LOG_TAG, "Handling..")

        if (intent == null) {
            Log.e(LOG_TAG, "onHandleIntent: intent == null")
            return
        }

        intent.action = getString(R.string.filter_key)

        when (intent.getStringExtra("download")) {
            "one" -> {
                Log.i(LOG_TAG, "Sending one image...")

                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(
                        intent.putExtra(
                            "downloadedImage", downloadImage(
                                intent.getParcelableExtra("imageToDownload")
                            )
                        )
                    )

            }
            "many" -> {
                Log.i(LOG_TAG, "Sending list of images...")

                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(intent.putExtra("images", downloadImages()))
            }

            else -> Log.e(LOG_TAG, "onHandleIntent: download isn't valid")
        }
    }

    private fun downloadImages(): ArrayList<out Parcelable> {
        val imagesJsonString = URL(ACCESSIBLE_API_URL)
            .openConnection()
            .getInputStream()
            .reader()
            .readText()

        ++pagesAmount

        return ImageFetcher.fetchListOfImages(imagesJsonString)
    }

    private fun downloadImage(image: Image?): Image? {
        return ImageFetcher.fetchImage(image)
    }
}