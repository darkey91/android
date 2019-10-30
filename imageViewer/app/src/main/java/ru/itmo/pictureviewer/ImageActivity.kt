package ru.itmo.pictureviewer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_image.*
import ru.itmo.pictureviewer.asynctask.ImageLoaderIntentService
import ru.itmo.pictureviewer.data.Image

class ImageActivity : AppCompatActivity() {
    private val LOG_TAG = "ImageActivity"

    private lateinit var receiver: ImageReceiver

    private var loadImageId: String? = ""
    private var loadImage: Image? = null

    private val cachedImages = mutableMapOf<String?, Image?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LOG_TAG, "onCreate..")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image)
        intent.apply {
            this@ImageActivity.loadImageId = getStringExtra("id")
            this@ImageActivity.loadImage = getParcelableExtra<Image?>("imageToOpen")
        }

        downloadImage()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.i(LOG_TAG, "onSaveInstanceState..")

        super.onSaveInstanceState(outState)
        val list = arrayListOf<Image?>()
        list.addAll(cachedImages.values)

        outState.putParcelableArrayList("cachedImages", list)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.i(LOG_TAG, "onRestoreInstanceState..")

        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getParcelableArrayList<Image>("cachedImages")?.forEach {
            cachedImages[it.id] = it
        }
    }

    override fun onStart() {
        Log.d(LOG_TAG, "onStart..")

        setReceiver()
        super.onStart()
    }

    override fun onStop() {
        Log.d(LOG_TAG, "onStop..")

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onStop()
    }

    private fun setReceiver() {
        Log.i(LOG_TAG, "Setting receiver..")

        receiver = ImageReceiver()

        val intentFilter = IntentFilter().apply {
            addAction(getString(R.string.filter_key))
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    private fun downloadImage() {
        val foundImage = cachedImages[loadImageId]
        if (foundImage != null) {
            setImage(foundImage)
        } else {
            image_progress_bar.visibility = View.VISIBLE

            val intent = Intent().apply {
                setClass(this@ImageActivity, ImageLoaderIntentService::class.java)
                putExtra("download", "one")
                putExtra("imageToDownload", loadImage)
            }

            startService(intent)
        }
    }

    private fun setImage(img: Image?) {
        Log.i(LOG_TAG, "Setting bitmap of big image...")

        image_progress_bar.visibility = View.INVISIBLE
        full_image.setImageBitmap(img?.image)
    }

    private inner class ImageReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getStringExtra("download").equals("one")) {
                val downloadedImage: Image? = intent?.getParcelableExtra("downloadedImage")
                setImage(downloadedImage)

                cachedImages[downloadedImage?.id] = downloadedImage
            }
        }

    }
}