package com.rameshreddy.findrestaurant.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rameshreddy.findrestaurant.R
import com.rameshreddy.findrestaurant.model.PlacesData
import com.rameshreddy.findrestaurant.ui.adapters.RestaurantListAdapter
import com.rameshreddy.findrestaurant.utils.RestaurantListener

/**
 * This fragment show the list of restaurant list in recycler view.
 *
 * @author Ramesh
 */
class RestaurantListFragment : Fragment() {

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout;
    private lateinit var restaurantListRecyclerView: RecyclerView
    private lateinit var mProgressBar: ProgressBar

    private lateinit var mRestaurantListener: RestaurantListener
    var mPlacesData: PlacesData = PlacesData("", ArrayList())
    lateinit var mRestaurantAdapter: RestaurantListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the custom view for this fragment layout
        val rootView = inflater!!.inflate(R.layout.fragment_restaurant_list, container, false)

        mProgressBar = rootView.findViewById(R.id.progressBar_restaurant_list)
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer)
        restaurantListRecyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView // Add this
        restaurantListRecyclerView.layoutManager = LinearLayoutManager(activity)

        if (context != null) {
            mRestaurantAdapter = RestaurantListAdapter(requireContext(), mPlacesData.restaurantsList)
            restaurantListRecyclerView.adapter = mRestaurantAdapter
        }

        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.setRefreshing(false);

            mRestaurantListener.refreshRestaurantList();
        }


        // Return the fragment view/layout
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            mRestaurantListener = context as RestaurantListener
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener.  */
        }

    }

    fun updateRestaurantData(placesData: PlacesData) {
        if (context != null) {
            mPlacesData = placesData
            restaurantListRecyclerView.adapter = RestaurantListAdapter(requireContext(), placesData.restaurantsList)

            if (placesData.restaurantsList.size > 0) {
                hideProgressBar()
            } else {
                showProgressBar()
            }
        }
    }

    fun showProgressBar() {
        if (mProgressBar != null)
            mProgressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        if (mProgressBar != null)
            mProgressBar.visibility = View.GONE
    }

}