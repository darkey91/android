package ru.itmo.pictureviewer.recycleView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.itmo.pictureviewer.R
import ru.itmo.pictureviewer.data.Image

class ImageAdapter(
    private val imageList: List<Image>?,
    val onClick: (Image?) -> Unit
) : RecyclerView.Adapter<ImageViewHolder>() {

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        //Why doesn't getString work here?
        holder.description.text = imageList?.get(position)?.description
        holder.image.setImageBitmap(imageList?.get(position)?.image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val holder = ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )

        holder.root.setOnClickListener {
            onClick(imageList?.get(holder.adapterPosition))
        }

        return holder
    }

    override fun getItemCount(): Int = imageList?.size ?: 0


}