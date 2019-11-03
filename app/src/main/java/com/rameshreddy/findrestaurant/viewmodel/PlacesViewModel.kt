package com.rameshreddy.findrestaurant.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.rameshreddy.findrestaurant.model.PlacesData
import com.rameshreddy.findrestaurant.repository.PlacesRepository

/**
 * This is viewmodel class to implement MVVM architecture.
 *
 * @author Ramesh
 */
class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository: PlacesRepository= PlacesRepository()


     fun getUsers(urlParams:String,currentLocation: Location): MutableLiveData<PlacesData> {

        return repository.getDataFromServer(urlParams,currentLocation)

    }

    fun getImageURL(imageUrl:String):MutableLiveData<Bitmap>{
        return repository.getImageURL(imageUrl)
    }

}