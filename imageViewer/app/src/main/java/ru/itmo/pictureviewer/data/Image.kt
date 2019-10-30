package ru.itmo.pictureviewer.data

import android.graphics.Bitmap
import android.os.Parcelable
import com.fasterxml.jackson.annotation.*
import kotlinx.android.parcel.Parcelize
import ru.itmo.pictureviewer.R
import android.content.res.Resources
import android.graphics.BitmapFactory


@Parcelize
data class Image constructor(
    @JsonProperty("id") var id: String,
    @JsonProperty("alt_description") var description: String? = "No description",

    @JsonIgnore var urlToBig: String = "",
    @JsonIgnore var urlToSmall: String = "",
    @JsonIgnore var isSmall: Boolean = true,
    @JsonIgnore var image: Bitmap? = null
) : Parcelable {

    constructor(other: Image): this(other.id, other.description, other.urlToBig, other.urlToSmall, false)

    fun setBitmap(byteArray: ByteArray) {
        image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    @JsonProperty("urls")
    fun setUrls(urls: Map<String, String>) {
        urlToBig = urls["regular"] ?: ""
        urlToSmall = urls["thumb"] ?: ""
    }

    //it doesn't want work :(
    @JsonProperty("alt_description")
    fun setDescriptionFronJson(description: String?) {
        if (description == null || description == "null" || description == "NULL") {
            this.description = Resources.getSystem().getString(R.string.no_description)
        }
    }

    fun checkDescription() {
        if (description == null || description == "null" || description == "NULL") {
            //doesn't work Resources.getSystem().getString(R.string.no_description)
            description = "No description"
        }
    }

}