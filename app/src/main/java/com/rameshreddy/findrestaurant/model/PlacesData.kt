package com.rameshreddy.findrestaurant.model

/**
 * This class stores the data of place
 *
 * @author Ramesh
 */
data class PlacesData(val nextPageToken:String,val restaurantsList: ArrayList<RestaurantData>) {
}