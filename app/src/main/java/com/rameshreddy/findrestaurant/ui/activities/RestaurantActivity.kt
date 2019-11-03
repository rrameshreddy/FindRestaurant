package com.rameshreddy.findrestaurant.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.rameshreddy.findrestaurant.R
import com.rameshreddy.findrestaurant.model.PlacesData
import com.rameshreddy.findrestaurant.ui.fragments.RestaurantListFragment
import com.rameshreddy.findrestaurant.ui.fragments.RestaurantMapFragment
import com.rameshreddy.findrestaurant.utils.Constants
import com.rameshreddy.findrestaurant.utils.RestaurantListener
import com.rameshreddy.findrestaurant.utils.Utils
import com.rameshreddy.findrestaurant.viewmodel.PlacesViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * This class fetch the restaurant list and show the data on UI
 *
 * @author Ramesh
 */
class RestaurantActivity : AppCompatActivity(), RestaurantListener, CoroutineScope {

    private val TAG = "RestaurantActivity";
    private val PERMISSION_REQUEST_CODE = 1
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var mPlacesData: PlacesData
    private  var mCurrentLatitude:Double=0.0
    private  var mCurrentLongitude:Double=0.0

    private val restaurantListFragment = RestaurantListFragment()
    private val restaurantMapFragment = RestaurantMapFragment()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val actionBar = supportActionBar // or getActionBar();
        supportActionBar!!.title = resources.getString(R.string.app_name) // set the top title
        val title = actionBar!!.title.toString() // get the title
        //actionBar.hide() // or even hide the actionbar

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        placesViewModel = ViewModelProvider(this).get(PlacesViewModel::class.java)


        // Get the support fragment manager instance
        val manager = supportFragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.fragment_container, restaurantListFragment)

        // Finishing the transition
        transaction.commit()

        getLastLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_restuarant, menu);

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId) {
            R.id.map -> {
                Log.i(TAG,"map action bar called");
                // Get the support fragment manager instance
                val manager = supportFragmentManager

                // Begin the fragment transition using support fragment manager
                val transaction = manager.beginTransaction()

                // Replace the fragment on container
                transaction.replace(R.id.fragment_container, restaurantMapFragment)
                    .addToBackStack(null)
                // Finishing the transition
                transaction.commit()

                restaurantMapFragment.updateRestaurantList(mPlacesData.restaurantsList)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    /**
     * This method refresh the restaurant list data.
     */
    override fun refreshRestaurantList() {
        getDataFromServerAndUpdateOnUI(false)
    }

    /**
     * This method gets the last current location
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                        //findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                        Log.i(TAG, "Current Location Coordinates: " + location.latitude + " , " + location.longitude);
                        mCurrentLatitude=location.latitude
                        mCurrentLongitude=location.longitude

                        getDataFromServerAndUpdateOnUI(true)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    /**
     * This method get the data from server and shows the data UI.
     *
     * @param showProgressBar flag to show progress or not
     */
    private fun getDataFromServerAndUpdateOnUI(showProgressBar:Boolean) {

        if (Utils.isConnectedToNetwork(this@RestaurantActivity)) {
            if(showProgressBar)
            restaurantListFragment.showProgressBar()
            var currentLocationString = mCurrentLatitude.toString()+"," + mCurrentLongitude.toString()
            val currentLocation = Location("")
            currentLocation.latitude = mCurrentLatitude
            currentLocation.longitude = mCurrentLongitude

            var locationURL: String =
                Constants.NEAR_BY_SEARCH_URL_PRE_FIX + Constants.API_KEY + Constants.NEAR_BY_SEARCH_URL_POST_FIX + "" + currentLocationString;
            launch {

                async(Dispatchers.IO) { placesViewModel.getUsers(locationURL,currentLocation) }.await().observe(this@RestaurantActivity,
                    Observer<PlacesData> { t ->
                        mPlacesData = t
                        restaurantListFragment.hideProgressBar()

                        restaurantListFragment.updateRestaurantData(mPlacesData)
                    })


            }
        } else {
            Toast.makeText(this@RestaurantActivity, resources.getText(R.string.network_error), Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * This method request for new location and fetchs the current user location.
     */
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        Log.i(TAG, "requestNew Locations");
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            //findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            //findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
            Log.i(TAG, "Current Location Coordinates: " + mLastLocation.latitude + " , " + mLastLocation.longitude);

            mCurrentLatitude=mLastLocation.latitude
            mCurrentLongitude=mLastLocation.longitude

            getDataFromServerAndUpdateOnUI(true)
        }
    }

    /**
     * This method returns is location settings enable or not.
     *
     * @return it will return flag whether location is enable or not.
     */
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /**
     * This method returns the flag the permissions granted or not.
     *
     * @return it will return the flag that requested permissions are granted or not.
     */
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    /**
     * This method request for permission to work all features.
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

}
