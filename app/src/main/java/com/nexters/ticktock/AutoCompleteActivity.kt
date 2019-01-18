package com.nexters.ticktock

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_auto_complete.*
import java.io.IOException


class AutoCompleteActivity : AppCompatActivity(), View.OnClickListener {

    val TAG:String = "AutoCompleteActivity"

    val GPS_ENABLE_REQUEST_CODE = 2001

    lateinit var geocoder: Geocoder
    lateinit var gps: GPSInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_complete)

        geocoder = Geocoder(this)
        gps = GPSInfo(this)
        isGPSConnected()
        btnShowLocation.setOnClickListener(this)

        setAutoComplete()
    }

    fun setAutoComplete() {
        val autocompleteFragment: SupportPlaceAutocompleteFragment? = SupportPlaceAutocompleteFragment()
        val fm: FragmentManager? = supportFragmentManager
        val ft: FragmentTransaction? = fm?.beginTransaction()
        ft?.replace(R.id.fragment_content, autocompleteFragment)
        ft?.commit()

        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = LatLng(place.latLng.latitude, place.latLng.longitude)
                Log.d(TAG, "${place.name}: ${place.address}")
                Log.d(TAG, "${place.latLng.latitude}, ${place.latLng.longitude}")
                tv_destination.text = "${place.name} ${place.address}"
            }

            override fun onError(p0: Status?) {
                Log.i(TAG, "An error occurred: ${p0}")
            }
        })
    }

    private fun getFromLocationToName(latLng: LatLng): Address? {
        var list: List<Address>? = null
        var address: Address? = null
        try {
            list = geocoder.getFromLocation(
                    latLng.latitude, // 위도
                    latLng.longitude, // 경도
                    1) // 얻어올 값의 개수
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("tag", "입출력 오류 - 서버에서 주소변환시 에러발생")
        }

        if (list != null) {
            if (list.isEmpty())
                Log.d("tag", "해당되는 주소 정보는 없습니다")
            else {
                address = list[0]
                Log.d("tag", list[0].toString())
            }
        }
        return address
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            GPS_ENABLE_REQUEST_CODE-> {
                gps = GPSInfo(this)
            }
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun isGPSConnected() {
        if (!checkLocationServicesStatus()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("위치 서비스 비활성화")
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하십시오.")
            builder.setCancelable(true)
            builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialogInterface, i ->
                val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
            })
            builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })
            builder.create().show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnShowLocation -> {
                if (gps.isGetLocation) {
                    val latitude = gps.latitude
                    var longitude = gps.longitude

                    var latLng = LatLng(latitude, longitude)

                    var address: Address? = null
                    while (address == null) {
                        longitude += 0.0000001
                        latLng = LatLng(latitude, longitude)
                        address = getFromLocationToName(latLng)
                    }
                    tv_current.text = "${address.getAddressLine(0)}"
                    Log.d("tag", "${latLng.latitude}, ${latLng.longitude}")
                } else {
                    tv_current.text = "cannot find"
                    Log.d("tag", "cannot find")
                }
            }
        }
    }
}
