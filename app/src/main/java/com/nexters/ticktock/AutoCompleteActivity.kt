package com.nexters.ticktock

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_auto_complete.*
import java.io.IOException


class AutoCompleteActivity : AppCompatActivity(), View.OnClickListener, DialogInterface.OnClickListener {

    val TAG:String = "AutoCompleteActivity"

    val GPS_ENABLE_REQUEST_CODE = 2001

    val PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM = 1;
    val PLACE_AUTOCOMPLETE_REQUEST_CODE_TO = 2;

    var isFrom:Boolean = true

    var checkNumberFrom = 0
    var checkNumberTo = 0

    var fromLatLng:LatLng? = null
    var toLatLng:LatLng? = null

    lateinit var geocoder: Geocoder
    lateinit var gps: GPSInfo


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_complete)

        geocoder = Geocoder(this)
        gps = GPSInfo(this)
        gps.isGPSConnected()
        layout_auto_from.setOnClickListener(this)
        layout_auto_to.setOnClickListener(this)
        btnShowLocation.setOnClickListener(this)
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
                    tv_destination.text = "${place.name}"
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    val status:Status = PlaceAutocomplete.getStatus(this, data)
                    Log.i(TAG, status.statusMessage)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // 사용자 입력에의한 종료
                }
            }
        }
    }

    // 자동완성 액티비티 생성
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

    // 좌표 주소 변환
    fun getFromLocationToName(latLng: LatLng): Address? {

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

    fun getGPSLocation(textView: TextView) {
        if (gps.isGetLocation) {
            var latitude = gps.latitude
            var longitude = gps.longitude

            var latLng = LatLng(latitude, longitude)
            var address = getFromLocationToName(latLng)

            while (address == null) {
                latitude += 0.00000001
                latLng = LatLng(latitude, longitude)
                address = getFromLocationToName(latLng)
            }

            textView.text = "${address.getAddressLine(0).substring(4)}" // (대한민국) 서울시~~
            if(isFrom)
                fromLatLng = LatLng(latitude, longitude)
            else
                toLatLng = LatLng(latitude, longitude)
        } else {
            textView.text = "잠시뒤 다시 시도해주세요"
            Log.d(TAG, "cannot find")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_auto_from -> {
                isFrom = true
                val builder = AlertDialog.Builder(this)
                val items = arrayOf("현위치", "검색")
                builder.setSingleChoiceItems(items, checkNumberFrom, this)
                builder.create().show()
            }

            R.id.layout_auto_to -> {
                isFrom = false
                val builder = AlertDialog.Builder(this)
                val items = arrayOf("현위치", "검색")
                builder.setSingleChoiceItems(items, checkNumberTo, this)
                builder.create().show()
            }

            R.id.btnShowLocation -> {

            }
        }
    }

    override fun onClick(dialog: DialogInterface?, index: Int) {

        dialog?.dismiss() // 클릭시 다이얼로그 사라짐

        if (isFrom) {
            checkNumberFrom = index
            when (index) {
                0 -> getGPSLocation(tv_current)

                1 -> createAutoComplete(PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM)
            }
        } else {
            checkNumberTo = index
            when (index) {
                0 -> getGPSLocation(tv_destination)

                1 -> createAutoComplete(PLACE_AUTOCOMPLETE_REQUEST_CODE_TO)
            }
        }
    }
}
