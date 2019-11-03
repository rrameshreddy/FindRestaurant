package com.rameshreddy.findrestaurant.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rameshreddy.findrestaurant.R
import com.rameshreddy.findrestaurant.model.RestaurantData
import com.rameshreddy.findrestaurant.ui.activities.RestaurantDetailsActivity
import kotlinx.android.synthetic.main.adapter_restaurant_item.view.*

/**
 * This class provide the adapter to show the list of restaurant list
 *
 * @author Ramesh
 */
class RestaurantListAdapter(val context: Context, val restaurantList: ArrayList<RestaurantData>) :
    RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantListAdapter.RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_restaurant_item, parent, false)

        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantListAdapter.RestaurantViewHolder, position: Int) {
        val restaurantData = restaurantList.get(position)
        holder.name.setText(restaurantData.name)
        holder.vicinity.setText(restaurantData.vicinity)


        var distance = "0 m"
        if (restaurantData.distance > 1000) {
            var distanceInkilometers = (restaurantData.distance).toFloat() / 1000

            distance = distanceInkilometers.toString() + " km"
        } else {
            distance = restaurantData.distance.toString() + " m"
        }

        holder.distance.setText(distance)

        // Calling the clickListener sent by the constructor
        holder?.itemView?.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("name", restaurantData.name)
            intent.putExtra("photoReference", restaurantData.photoReference)
            context.startActivity(intent);
        }

    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name_tv
        val vicinity = itemView.vicinity_tv
        val icon = itemView.icon_iv
        val distance = itemView.distance_tv
    }

}