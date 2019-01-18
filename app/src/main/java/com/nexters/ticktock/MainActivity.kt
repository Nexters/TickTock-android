package com.nexters.ticktock

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val autocompleteFragment:SupportPlaceAutocompleteFragment? = SupportPlaceAutocompleteFragment()
        val fm:FragmentManager? = supportFragmentManager
        val ft:FragmentTransaction? = fm?.beginTransaction()
        ft?.replace(R.id.fragment_content, autocompleteFragment)
        ft?.commit()

        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = LatLng(place.latLng.latitude, place.latLng.longitude)
                Log.d("TAG", "${place.name}: ${place.address}")
                Log.d("TAG", "${place.latLng.latitude}, ${place.latLng.longitude}")
            }

            override fun onError(p0: Status?) {
                Log.i("TAG", "An error occurred: ${p0}")
            }
        })
    }
}
