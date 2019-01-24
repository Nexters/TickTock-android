package com.nexters.ticktock

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
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
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.model.LatLng
import com.nexters.ticktock.databinding.ActivityAutoCompleteBinding
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_auto_complete.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class AutoCompleteActivity : AppCompatActivity(), View.OnClickListener, DialogInterface.OnClickListener, OnResultCallbackListener {

    private lateinit var binding: ActivityAutoCompleteBinding
    private val TAG:String = "AutoCompleteActivity"

    private val GPS_ENABLE_REQUEST_CODE = 2001

    private val PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM = 1;
    private val PLACE_AUTOCOMPLETE_REQUEST_CODE_TO = 2;

    private var isFrom:Boolean = true

    private var checkNumberFrom = 0
    private var checkNumberTo = 0

    private var fromLatLng:LatLng? = null
    private var toLatLng:LatLng? = null

    private lateinit var geocoder: Geocoder
    private lateinit var gps: GPSInfo

    private lateinit var odsayService: ODsayService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auto_complete)

        gps = GPSInfo(this) // GPS
        gps.isGPSConnected()
        geocoder = Geocoder(this) // 좌표 주소 변환 객체

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService = ODsayService.init(this, getResources().getString(R.string.odsay_key));
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        binding.layoutAutoFrom.setOnClickListener(this)
        binding.layoutAutoTo.setOnClickListener(this)
        binding.btnShowLocation.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode){
            GPS_ENABLE_REQUEST_CODE -> gps = GPSInfo(this)

            PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val place:Place = PlaceAutocomplete.getPlace(this, data)
                        binding.tvCurrent.text = "${place.name}"

                        fromLatLng = LatLng(place.latLng.latitude, place.latLng.longitude)
                    }
                    PlaceAutocomplete.RESULT_ERROR -> {
                        val status:Status = PlaceAutocomplete.getStatus(this, data)
                        Log.i(TAG, status.statusMessage)
                    }
                    Activity.RESULT_CANCELED -> {
                        // 사용자 입력에의한 종료
                    }
                }
            }

            PLACE_AUTOCOMPLETE_REQUEST_CODE_TO -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val place: Place = PlaceAutocomplete.getPlace(this, data)
                        binding.tvDestination.text = "${place.name}"

                        toLatLng = LatLng(place.latLng.latitude, place.latLng.longitude)
                    } PlaceAutocomplete.RESULT_ERROR -> {
                        val status: Status = PlaceAutocomplete.getStatus(this, data)
                        Log.i(TAG, status.statusMessage)
                    } Activity.RESULT_CANCELED -> {
                        // 사용자 입력에의한 종료
                    }
                }
            }
        }
    }

    // 자동완성 액티비티 생성
    private fun createAutoComplete(requestCode: Int) {

        try {
            val typeFilter:AutocompleteFilter = AutocompleteFilter.Builder().setCountry("KR").build()

            val intent:Intent = PlaceAutocomplete
                                .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(typeFilter)
                                .build(this)

            startActivityForResult(intent, requestCode)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }

    // 좌표 주소 변환
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
            Log.e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생")
        }

        if (list != null) {
            if (list.isEmpty())
                Log.d(TAG, "해당되는 주소 정보는 없습니다")
            else {
                address = list[0]
                Log.d("tag", list[0].toString())
            }
        }
        return address
    }

    private fun getGPSLocation(textView: TextView) {
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

            // textView.text = "${address.getAddressLine(0).substring(4)}" // (대한민국) 서울시~~
            textView.text = "현위치"
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
            binding.layoutAutoFrom.id -> {
                isFrom = true
                val builder = AlertDialog.Builder(this)
                val items = arrayOf("현위치", "검색")
                builder.setSingleChoiceItems(items, checkNumberFrom, this)
                builder.create().show()
            }

            binding.layoutAutoTo.id -> {
                isFrom = false
                val builder = AlertDialog.Builder(this)
                val items = arrayOf("현위치", "검색")
                builder.setSingleChoiceItems(items, checkNumberTo, this)
                builder.create().show()
            }

            binding.btnShowLocation.id -> {
                odsayService.requestSearchPubTransPath(
                        fromLatLng!!.longitude.toString(),
                        fromLatLng!!.latitude.toString(),
                        toLatLng!!.longitude.toString(),
                        toLatLng!!.latitude.toString(),
                        "0",
                        "0",
                        "0",
                        this)
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, index: Int) {

        dialog?.dismiss() // 클릭시 다이얼로그 사라짐

        if (isFrom) {
            checkNumberFrom = index
            when (index) {
                0 -> getGPSLocation(binding.tvCurrent)

                1 -> createAutoComplete(PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM)
            }
        } else {
            checkNumberTo = index
            when (index) {
                0 -> getGPSLocation(binding.tvDestination)

                1 -> createAutoComplete(PLACE_AUTOCOMPLETE_REQUEST_CODE_TO)
            }
        }
    }

    override fun onSuccess(odsayData: ODsayData, api: API?) {
        try {
            // API Value 는 API 호출 메소드 명을 따라갑니다.
            if (api == API.SEARCH_PUB_TRANS_PATH) {
                val jArray: JSONArray = odsayData.getJson().getJSONObject("result").getJSONArray("path")
                val jObject: JSONObject = jArray.getJSONObject(0).getJSONObject("info") // 최단시간
                val totalTime = jObject.getInt("totalTime")

                val intent = Intent()
                intent.putExtra("totalTime", totalTime)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }catch (e: JSONException) {
            e.printStackTrace();
        }
    }

    override fun onError(i: Int, s: String?, api: API?) {
        if (api == API.SEARCH_PUB_TRANS_PATH) {}
    }
}
