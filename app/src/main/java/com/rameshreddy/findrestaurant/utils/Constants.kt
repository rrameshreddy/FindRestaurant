package com.rameshreddy.findrestaurant.utils

/**
 * This stores the constants of the project
 *
 * @author Ramesh
 */
object Constants {
    const val NEAR_BY_SEARCH_URL_PRE_FIX ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=restaurant&key="
    const val NEAR_BY_SEARCH_URL_POST_FIX="&rankby=distance&location="
    const val NEXT_PAGE_TOKEN = "&next_page_token="
    const val PHOTO_URL="https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="
    const val PHOTO_KEY="&key="
    // Update API key
    const val API_KEY="API_KEY"
}