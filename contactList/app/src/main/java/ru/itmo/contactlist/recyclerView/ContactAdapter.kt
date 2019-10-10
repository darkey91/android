package ru.itmo.contactlist.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import ru.itmo.contactlist.R
import ru.itmo.contactlist.data.Contact

class ContactAdapter(
    val contacts: List<Contact>,
    val onClick: (Contact) -> Unit) : RecyclerView.Adapter<ContactViewHolder>()  {

    override fun getItemCount(): Int = contacts.size;

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.names.text = contacts[position].name
        holder.number.text = contacts[position].phoneNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
       val holder = ContactViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.list_item,
               parent,
               false
           )
       )

        holder.root.setOnClickListener {
            onClick(contacts[holder.adapterPosition])
        }
        return holder
    }
}