package ru.itmo.pictureviewer.utils

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.itmo.pictureviewer.data.Image
import java.net.URL

object ImageFetcher {
    private const val LOG_TAG = "ImageFetcher"

    private val mapper = jacksonObjectMapper()

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)

        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
    }

    fun fetchImage(img: Image?): Image? {
        Log.i(LOG_TAG, "Fetching bitmap from bigImage")

        if (img == null) {
            Log.e(LOG_TAG, "fetchImage: img is null")
            return null
        }

        val bigImage = Image(img)
        bigImage.setBitmap(
            URL(img.urlToBig).readBytes()
        )
        return bigImage
    }

    fun fetchListOfImages(imagesJsonString: String): ArrayList<Image> {
        Log.i(LOG_TAG, "Fetching list of images from $imagesJsonString")


        val listOfImages: ArrayList<Image> = mapper.readValue(imagesJsonString)
        listOfImages.forEach {
            it.setBitmap(
                URL(it.urlToSmall).readBytes()
            )

            it.checkDescription()
        }
        return listOfImages
    }
}