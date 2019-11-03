package com.rameshreddy.findrestaurant.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rameshreddy.findrestaurant.model.PlacesData
import com.rameshreddy.findrestaurant.model.RestaurantData
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * This is repository class to do network operation and provide data to view model class
 *
 * @author Ramesh
 */
class PlacesRepository {

    /**
     * This method get the restaurant list from server.
     *
     * @param urlWithParams url to request the data
     * @param currentCoordinates this parameter provide current coordinates
     */
    fun getDataFromServer(urlWithParams: String, currentCoordinates: Location): MutableLiveData<PlacesData> {

        val placesData: MutableLiveData<PlacesData> = MutableLiveData()

        try {
            val mURL = URL(urlWithParams)

            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "GET"

                println("URL : $url")
                println("Response Code : $responseCode")

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    println("Response : $response")

                    val jsonObject = JSONObject(response.toString())
                    var nextToken = jsonObject.getString("next_page_token")

                    var jsonArrayResults: JSONArray = jsonObject.getJSONArray("results")

                    var restaurantsList = ArrayList<RestaurantData>()

                    for (i in 0..jsonArrayResults.length() - 1) {
                        var json_objectdetail: JSONObject = jsonArrayResults.getJSONObject(i)

                        var icon = json_objectdetail.getString("icon")
                        var name = json_objectdetail.getString("name")
                        var vicinity = json_objectdetail.getString("vicinity")


                        var photoReference = ""
                        if (json_objectdetail.has("photos")) {
                            var photos = json_objectdetail.getJSONArray("photos")
                            if (photos.length() > 0)
                                photoReference = photos.getJSONObject(0).getString("photo_reference")
                        }

                        var latitude = 0.0
                        var longitude = 0.0
                        var distance = 0
                        if (json_objectdetail.has("geometry")) {
                            var geometryObj = json_objectdetail.getJSONObject("geometry")
                            var locationObj = geometryObj.getJSONObject("location");

                            latitude = locationObj.getDouble("lat")
                            longitude = locationObj.getDouble("lng")

                            val endPoint = Location(name)
                            endPoint.latitude = latitude
                            endPoint.longitude = longitude

                            distance = (currentCoordinates.distanceTo(endPoint)).toInt()
                        }

                        var model = RestaurantData(icon, name, vicinity, photoReference, latitude, longitude, distance)
                        restaurantsList.add(model)
                    }

                    var finalData = PlacesData(nextToken, restaurantsList)

                    GlobalScope.launch(Dispatchers.Main) {
                        placesData.setValue(finalData)
                    }


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return placesData;
    }

    /**
     * This method provides bitmap image using photo reference
     *
     * @param urlWithParams url to request image data
     */
    fun getImageURL(urlWithParams: String): MutableLiveData<Bitmap> {

        val imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()


        val mURL = URL(urlWithParams)

        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "GET"

            println("URL : $url")
            println("Response Code : $responseCode")

            if (responseCode == 200) {
                var bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                GlobalScope.launch(Dispatchers.Main) {
                    imageBitmap.setValue(bitmap)
                }
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    imageBitmap.setValue(null)
                }
            }
        }


        return imageBitmap
    }

}