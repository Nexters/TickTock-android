package com.nexters.ticktock

import android.app.Activity
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_auto_complete.*
import java.io.IOException
import javax.xml.transform.Templates


class AutoCompleteActivity : AppCompatActivity(), View.OnClickListener, DialogInterface.OnClickListener {

    val TAG:String = "AutoCompleteActivity"

    val GPS_ENABLE_REQUEST_CODE = 2001
    val PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM = 1;
    val PLACE_AUTOCOMPLETE_REQUEST_CODE_TO = 2;

    lateinit var geocoder: Geocoder
    lateinit var gps: GPSInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_complete)

        geocoder = Geocoder(this)
        gps = GPSInfo(this)
        isGPSConnected()
        layout_auto_from.setOnClickListener(this)
        layout_auto_to.setOnClickListener(this)
        btnShowLocation.setOnClickListener(this)
    }

    fun createAutoComplete(requestCode: Int) {

        try {
            val intent:Intent =
            PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, requestCode);
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
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
            GPS_ENABLE_REQUEST_CODE -> gps = GPSInfo(this)

            PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place:Place = PlaceAutocomplete.getPlace(this, data)
                    Log.i(TAG, "Place: " + place.getName())
                    tv_current.text = "${place.name}"
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    val status:Status = PlaceAutocomplete.getStatus(this, data)
                    Log.i(TAG, status.statusMessage)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // 사용자 입력에의한 종료
                }
            }

            PLACE_AUTOCOMPLETE_REQUEST_CODE_TO -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place:Place = PlaceAutocomplete.getPlace(this, data)
                    Log.i(TAG, "Place: " + place.getName())
                    tv_to.text = "${place.name}"
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    val status:Status = PlaceAutocomplete.getStatus(this, data)
                    Log.i(TAG, status.statusMessage)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // 사용자 입력에의한 종료
                }
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
            R.id.layout_auto_from -> {
                val builder = AlertDialog.Builder(this)
                val items = arrayOf("현위치", "검색")
                builder.setSingleChoiceItems(items, 0, this)
                builder.create().show()
                // val customDialog = CustomDialog(this)
                // customDialog.callFunction()
                // createAutoComplete(PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM)
            }

            R.id.layout_auto_to -> createAutoComplete(PLACE_AUTOCOMPLETE_REQUEST_CODE_TO)

            R.id.btnShowLocation -> {
                if (gps.isGetLocation) {
                    var latitude = gps.latitude
                    var longitude = gps.longitude

                    var latLng = LatLng(latitude, longitude)

                    var address: Address? = null
                    while (address == null) {
                        latitude += 0.00000001
                        latLng = LatLng(latitude, longitude)
                        address = getFromLocationToName(latLng)
                    }
                    tv_current.text = "${address.getAddressLine(0)}"
                    Log.d(TAG, "${latLng.latitude}, ${latLng.longitude}")
                } else {
                    tv_current.text = "잠시뒤 다시 시도해주세요"
                    Log.d(TAG, "cannot find")
                }
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, index: Int) {
        val dialog:DialogInterface? = dialog
        dialog?.dismiss()
         when (index) {
             0 -> {

             }

             1 -> {
                 createAutoComplete(PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM)
             }
         }
    }
}
