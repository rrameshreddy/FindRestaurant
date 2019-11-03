package com.rameshreddy.findrestaurant.ui.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rameshreddy.findrestaurant.R
import com.rameshreddy.findrestaurant.utils.Constants
import com.rameshreddy.findrestaurant.utils.TouchImageView
import com.rameshreddy.findrestaurant.viewmodel.PlacesViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * This class shows the full image of restaurant and provide the feature to zoom in and zoom out.
 *
 * @author Ramesh
 */
class RestaurantDetailsActivity : AppCompatActivity(), CoroutineScope {

    private val TAG: String = "RestaurantDetails"
    private lateinit var mSelectedPlaceImageVeiw: TouchImageView
    private lateinit var mNoImageAvailableTextView: TextView
    private lateinit var mProgressBar: ProgressBar

    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var mImagBitmap: Bitmap
    private lateinit var mPhotoReference: String
    private lateinit var mName: String

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        mSelectedPlaceImageVeiw = findViewById(R.id.selected_place_iv)
        mNoImageAvailableTextView = findViewById(R.id.image_not_available_tv)
        mProgressBar = findViewById(R.id.progressBar_details)

        mPhotoReference = intent.getStringExtra("photoReference")
        mName = intent.getStringExtra("name")

        placesViewModel = ViewModelProvider(this).get(PlacesViewModel::class.java)

        getImageFromServer()

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    /**
     * This method get photo bitmap from server and display on UI.
     */
    private fun getImageFromServer() {
        launch {

            async(Dispatchers.IO) { placesViewModel.getImageURL(Constants.PHOTO_URL + "" + mPhotoReference + "" + Constants.PHOTO_KEY + Constants.API_KEY) }.await()
                .observe(this@RestaurantDetailsActivity,
                    Observer<Bitmap> { t ->

                        mProgressBar.visibility=View.GONE
                        if (t != null) {
                            mImagBitmap = t
                            Log.i(TAG, "image URL: " + mImagBitmap)
                            mNoImageAvailableTextView.visibility = View.GONE
                            mSelectedPlaceImageVeiw.setImageBitmap(mImagBitmap)
                        } else {
                            mNoImageAvailableTextView.visibility = View.VISIBLE
                        }

                    })


        }
    }


}
