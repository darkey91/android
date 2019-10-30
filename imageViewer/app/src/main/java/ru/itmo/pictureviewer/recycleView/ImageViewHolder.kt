package ru.itmo.pictureviewer.recycleView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ImageViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val image: ImageView = root.image
    val description: TextView = root.description
}

