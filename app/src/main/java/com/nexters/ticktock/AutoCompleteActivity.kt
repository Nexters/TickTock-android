package com.nexters.ticktock

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.nexters.ticktock.databinding.ActivityAutoCompleteBinding
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.collections.ArrayList


class AutoCompleteActivity : AppCompatActivity(), View.OnClickListener, OnResultCallbackListener, PlaceAutocompleteAdapter.PlaceAutoCompleteInterface, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, TextWatcher {

    private lateinit var binding: ActivityAutoCompleteBinding // 데이터 바인딩
    private val TAG:String = "AutoCompleteActivity"

    private val GPS_ENABLE_REQUEST_CODE = 2001

    private var latLng:LatLng? = null

    private var fromLatLng:LatLng? = null
    private var toLatLng:LatLng? = null

    private lateinit var geocoder: Geocoder // 좌표 - 주소 변환
    private lateinit var gps: GPSInfo // gps

    private lateinit var googleApiClient: GoogleApiClient // 구글 검색 api 사용을 위함
    private lateinit var placeAutocompleteAdapter: PlaceAutocompleteAdapter // 리사이클러뷰 어댑터

    private lateinit var odsayService: ODsayService // 오디세이

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

        binding.btnSaveData.setOnClickListener(this)

        initPlace()
    }

    // 구글 클라이언트 연결
    override fun onStart() {
        googleApiClient.connect()
        super.onStart()
    }

    override fun onStop() {
        googleApiClient.disconnect()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // gps 설정 변경 후 재연결
            GPS_ENABLE_REQUEST_CODE -> gps = GPSInfo(this)
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

    // TODO 인자변경
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
            // TODO
            latLng = LatLng(latitude, longitude)
        } else {
            textView.text = "잠시뒤 다시 시도해주세요"
            Log.d(TAG, "cannot find")
        }
    }

    /*
     * 구글 검색 api 사용을 위한 메소드 시작
     */
    fun initPlace() {
        googleApiClient = GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .build()

        binding.listSearch.setHasFixedSize(true)
        binding.listSearch.layoutManager = LinearLayoutManager(this)

        val typeFilter = AutocompleteFilter.Builder()
                .setCountry("KR")
                .build()

        placeAutocompleteAdapter = PlaceAutocompleteAdapter(this, R.layout.item_search, googleApiClient, null, typeFilter)
        binding.listSearch.adapter = placeAutocompleteAdapter

        binding.edSearchFrom.addTextChangedListener(this)
        binding.edSearchTo.addTextChangedListener(this)
    }

    override fun onPlaceClick(resultList: ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete>, position: Int) {
        if(resultList.size > 0) {
            try {
                val placeId: String = resultList.get(position).placeId.toString()
                val placeResult: PendingResult<PlaceBuffer> = Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                placeResult.setResultCallback {
                    if(it.count == 1) {
                        // 이곳에서 키워드를 선택한 데이터를 처리한다.
                        val location = Location(it.get(0).name.toString())
                        location.latitude = it.get(0).latLng.latitude
                        location.longitude = it.get(0).latLng.longitude
                    } else {
                        // error
                    }
                }
            } catch (e: Exception) {

            } finally {
                runOnUiThread { run {
                    placeAutocompleteAdapter.clearList()
                    binding.listSearch.setVisibility(View.GONE)
                } }
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (count > 0) {
            binding.listSearch.setVisibility(View.VISIBLE)
        } else {
            placeAutocompleteAdapter.clearList()
            binding.listSearch.setVisibility(View.GONE)
        }
        if (!s.toString().equals("") && googleApiClient.isConnected) {
            placeAutocompleteAdapter.filter.filter(s.toString())
        } else if (!googleApiClient.isConnected) {
            Log.e("", "NOT CONNECTED")
        }
    }
    /*
    * 종료
    */

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnSaveData.id -> {
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

    /*
     * 오디세이 api 사용을 위한 메소드 시작
     */
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
    /*
     * 종료
     */
}


