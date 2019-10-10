package ru.itmo.contactlist.recyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ContactViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val names = root.name
    val number = root.phone_number

}