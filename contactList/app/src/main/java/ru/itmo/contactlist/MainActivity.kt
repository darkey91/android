package ru.itmo.contactlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.itmo.contactlist.data.fetchAllContacts
import ru.itmo.contactlist.recyclerView.ContactAdapter
import android.net.Uri


class MainActivity : AppCompatActivity() {
    //todo ask: объясните зачем эта штука
    private val myRequestId = 173

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showContacts()
        } else {
            onRequestPermission()
        }
    }


    private fun showContacts() {
        setContentView(R.layout.activity_main)

        //todo ask: context is available from onCreate, but from onReq.. is not. What is the diff?

        val contactList = fetchAllContacts()
        val viewManager = LinearLayoutManager(this)

        recycle_view.apply {
            layoutManager = viewManager
            adapter = ContactAdapter(contactList) {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + (it.phoneNumber))
                startActivity(callIntent)
            }
        }
        Toast.makeText(
            this@MainActivity,
            "Found ${contactList.size}",
            Toast.LENGTH_SHORT
        )
            .show()

    }

    fun onRequestContactPermission(view: View) {
        onRequestPermission()
    }

    fun onLoadContactList(view: View) {
        showContacts()
    }

    private fun onRequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            myRequestId
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            myRequestId -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showContacts()
                } else {
                    setContentView(R.layout.activity_permission_denied)
                }
                return

            }
        }
    }
}
