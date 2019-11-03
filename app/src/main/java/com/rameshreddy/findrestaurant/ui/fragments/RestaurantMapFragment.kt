package com.rameshreddy.findrestaurant.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.rameshreddy.findrestaurant.R
import com.rameshreddy.findrestaurant.model.RestaurantData
import java.lang.Exception
import android.content.Intent
import android.net.Uri


/**
 * This fragment shows the list of restaurants on map
 *
 * @author Ramesh
 */
class RestaurantMapFragment : Fragment(), OnMapReadyCallback {


    private var mGoogleMap: GoogleMap? = null
    private lateinit var mRestaurantList: ArrayList<RestaurantData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the custom view for this fragment layout
        val rootView = inflater!!.inflate(R.layout.fragment_restaurant_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return rootView
    }

    fun updateRestaurantList(restaurantList: ArrayList<RestaurantData>) {
        mRestaurantList = restaurantList;
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap

        for (i in 0..mRestaurantList.size - 1) {
            var data = mRestaurantList.get(i)

            mGoogleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(data.latitude, data.longitude))
                    .title(data.name)
                    .snippet(data.vicinity)
            )

            if(mRestaurantList.size-1==i){
                //move map camera
                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(data.latitude, data.longitude)))
                mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(13f))
            }

        }

        mGoogleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                val gmmIntentUri = Uri.parse("geo:"+marker.position.latitude.toString()+","+marker.position.longitude.toString())
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(mapIntent)
                }

                return false
            }
        })


    }


}