package ru.itmo.pictureviewer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_main.*
import ru.itmo.pictureviewer.asynctask.ImageLoaderIntentService
import ru.itmo.pictureviewer.data.Image
import ru.itmo.pictureviewer.recycleView.ImageAdapter

private var images = ArrayList<Image>()

class MainActivity : AppCompatActivity() {
    private val LOG_TAG = "MainActivity"


    private var isLoading = false
    private lateinit var receiver: ImageReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LOG_TAG, "onCreate..")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        if (images.size == 0) {
            downloadImages()
        } else {
            progress_bar.visibility = View.INVISIBLE
        }

        initRecycleView()
    }

    //Падает, потому что
    // java.lang.RuntimeException: android.os.TransactionTooLargeException: data parcel size 1408364 bytes
    /*
    override fun onSaveInstanceState(outState: Bundle) {
        Log.i(LOG_TAG, "onSaveInstanceState..")

        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("imageList", images)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.i(LOG_TAG, "onRestoreInstanceState..")

        super.onRestoreInstanceState(savedInstanceState)
        images.addAll(savedInstanceState.getParcelableArrayList<Image>("imageList") as ArrayList<Image>)
    }
*/
    override fun onStart() {
        Log.d(LOG_TAG, "onStart..")

        setReceiver()
        super.onStart()
    }

    override fun onStop() {
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

    private fun initRecycleView() {

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ImageAdapter(images) {

                Log.i(LOG_TAG, "Changing intent...")
                val imageIntent = Intent().apply {
                    setClass(applicationContext, ImageActivity::class.java)
                    putExtra("id", it?.id)
                    putExtra("imageToOpen", it)
                }

                startActivity(imageIntent)
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!isLoading &&
                        (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == (layoutManager as LinearLayoutManager).itemCount - 1
                    ) {
                        downloadImages()
                        isLoading = true
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            }

            )
        }
    }


    private fun downloadImages() {
        progress_bar.visibility = View.VISIBLE
        val intent = Intent().apply {
            setClass(this@MainActivity, ImageLoaderIntentService::class.java)
            putExtra("download", "many")
        }
        startService(intent)
    }


    private inner class ImageReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getStringExtra("download").equals("many")) {
                images.addAll(intent?.getParcelableArrayListExtra("images") ?: emptyList())
//todo
                recycler_view.adapter?.notifyDataSetChanged()
                isLoading = false
                progress_bar.visibility = View.INVISIBLE
            }
        }

    }
}
