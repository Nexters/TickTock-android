package com.nexters.ticktock.autocomplete

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityAutoCompleteBinding
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception
import kotlin.collections.ArrayList


class AutoCompleteActivity : AppCompatActivity(), OnResultCallbackListener, PlaceAutocompleteRecyclerAdapter.PlaceAutoCompleteInterface, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, View.OnFocusChangeListener {

    private lateinit var binding: ActivityAutoCompleteBinding // 데이터 바인딩
    private val TAG:String = "AutoCompleteActivity"

    private var isFrom:Boolean = true

    private var fromLatLng:LatLng? = null
    private var toLatLng:LatLng? = null

    private lateinit var googleApiClient: GoogleApiClient // 구글 검색 api 사용을 위함
    private lateinit var placeAutocompleteRecyclerAdapter: PlaceAutocompleteRecyclerAdapter // 자동완성 리사이클러뷰 어댑터
    private lateinit var directionRecyclerAdapter: DirectionRecyclerAdapter // 길찾기 리사이클러뷰 어댑터

    private lateinit var odsayService: ODsayService // 오디세이

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auto_complete)

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService = ODsayService.init(this, getResources().getString(R.string.odsay_key));
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        initPlace()
        getGPSLocation() // gps 내위치 정보 있다면 set
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

    fun getGPSLocation() {
        // val gps = intent.getParcelableExtra<GPSInfo.Result>("GPS_RESULT")
        val gps: GPSInfo.Result = GPSInfo.Result("", 0.0, 0.0)
        if (gps.address != "") {
            binding.edSearchFrom.setText("내위치: ${gps.address}")
            fromLatLng = LatLng(gps.latitude, gps.longitude)
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

        binding.recyclerviewAutocomplete.setHasFixedSize(true)
        binding.recyclerviewAutocomplete.layoutManager = LinearLayoutManager(this)

        val typeFilter = AutocompleteFilter.Builder()
                .setCountry("KR")
                .build()

        placeAutocompleteRecyclerAdapter = PlaceAutocompleteRecyclerAdapter(this, googleApiClient, null, typeFilter)
        binding.recyclerviewAutocomplete.adapter = placeAutocompleteRecyclerAdapter

        binding.recyclerviewDirection.layoutManager = LinearLayoutManager(this)
        directionRecyclerAdapter = DirectionRecyclerAdapter(this, windowManager.defaultDisplay, ArrayList())
        binding.recyclerviewDirection.adapter = directionRecyclerAdapter
        binding.recyclerviewDirection.addOnItemTouchListener(RecyclerItemClickListener(this, binding.recyclerviewDirection, object: RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                val intent = Intent()
                intent.putExtra("totalTime", directionRecyclerAdapter.transPath.get(position).path.totalTime)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onLongItemClick(view: View, position: Int) {}
        }))

        binding.edSearchFrom.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.recyclerviewAutocomplete.recycledViewPool.clear()
                placeAutocompleteRecyclerAdapter.clearList()
                if (count > 0) binding.recyclerviewAutocomplete.setVisibility(View.VISIBLE)
                else binding.recyclerviewAutocomplete.setVisibility(View.GONE)

                if (!s.toString().equals("") && googleApiClient.isConnected) {
                    binding.layoutDirection.visibility = View.GONE // 텍스트가 변경될때 길찾기 정보는 보여지지 않음
                    placeAutocompleteRecyclerAdapter.filter.filter(s.toString())
                } else if (!googleApiClient.isConnected) {
                    Log.e("", "NOT CONNECTED")
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edSearchTo.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.recyclerviewAutocomplete.recycledViewPool.clear()
                placeAutocompleteRecyclerAdapter.clearList()
                if (count > 0) binding.recyclerviewAutocomplete.setVisibility(View.VISIBLE)
                else binding.recyclerviewAutocomplete.setVisibility(View.GONE)

                if (!s.toString().equals("") && googleApiClient.isConnected) {
                    binding.layoutDirection.visibility = View.GONE // 텍스트가 변경될때 길찾기 정보는 보여지지 않음
                    placeAutocompleteRecyclerAdapter.filter.filter(s.toString())
                } else if (!googleApiClient.isConnected) {
                    Log.e("", "NOT CONNECTED")
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edSearchFrom.setOnFocusChangeListener(this)
        binding.edSearchTo.setOnFocusChangeListener(this)
    }

    override fun onPlaceClick(resultList: ArrayList<PlaceAutocompleteRecyclerAdapter.PlaceAutocomplete>, position: Int) {
        if(resultList.size > 0) {
            try {
                val placeId: String = resultList.get(position).placeId.toString()
                val placeTitle: String = resultList.get(position).title.toString()

                val placeResult: PendingResult<PlaceBuffer> = Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                placeResult.setResultCallback {
                    if(it.count == 1) { // 선택한 위치 좌표 set
                        val latLng = LatLng(it.get(0).latLng.latitude, it.get(0).latLng.longitude)
                        setEditTextLatLng(placeTitle, latLng)
                        binding.recyclerviewAutocomplete.recycledViewPool.clear()
                        placeAutocompleteRecyclerAdapter.clearList() // finally 부분 ui스레드 종료시에도 해당 콜백메소드가 실행되어
                        binding.recyclerviewAutocomplete.setVisibility(View.GONE) // 정상적으로 리사이클뷰가 닫히지 않아 이중으로 작성 **
                    } else {
                        // error
                    }
                }
            } catch (e: Exception) {

            } finally {
                runOnUiThread { run {
                    binding.recyclerviewAutocomplete.recycledViewPool.clear()
                    placeAutocompleteRecyclerAdapter.clearList()
                    binding.recyclerviewAutocomplete.setVisibility(View.GONE)
                } }
            }
        }
    }

    private fun setEditTextLatLng(placeTitle:String, latLng: LatLng?) {
        if (isFrom) {
            fromLatLng = latLng
            binding.edSearchFrom.setText(placeTitle)
        } else {
            toLatLng = latLng
            binding.edSearchTo.setText(placeTitle)
        }
        binding.recyclerviewDirection.recycledViewPool.clear()
        getDirection() // 장소 선택될 경우 텍스트 채워지면서 길찾기 수행
    }

    override fun onConnectionFailed(p0: ConnectionResult) {}

    override fun onConnected(p0: Bundle?) {}

    override fun onConnectionSuspended(p0: Int) {}

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            binding.edSearchFrom.id -> {
                isFrom = true
                binding.recyclerviewAutocomplete.setVisibility(View.GONE)
            }
            binding.edSearchTo.id -> {
                isFrom = false
                binding.recyclerviewAutocomplete.setVisibility(View.GONE)
            }
        }
    }
    /*
    * 종료
    */

    /*
     * 오디세이 api 사용을 위한 메소드 시작
     */
    fun getDirection() {
        if(fromLatLng != null && toLatLng != null) {
            odsayService.requestSearchPubTransPath(
                    fromLatLng!!.longitude.toString(),
                    fromLatLng!!.latitude.toString(),
                    toLatLng!!.longitude.toString(),
                    toLatLng!!.latitude.toString(),
                    "0",
                    "0",
                    "0",
                    this)
            binding.recyclerviewAutocomplete.visibility = View.GONE
        }
    }

    override fun onSuccess(odsayData: ODsayData, api: API?) {
        try {
            // API Value 는 API 호출 메소드 명을 따라갑니다.
            if (api == API.SEARCH_PUB_TRANS_PATH) {
                val jArray: JSONArray = odsayData.getJson().getJSONObject("result").getJSONArray("path")
                val transPathList = ArrayList<SearchPubTransPath>()

                for (i in 0..jArray.length()-1) { // 출력결과 만큼 출력 jArray.length()
                    val jObject = jArray.getJSONObject(i)
                    val jInfo = jObject.getJSONObject("info")
                    val jSubPath = jObject.getJSONArray("subPath")
                    val subPathList = ArrayList<SearchPubTransPath.SubPath>()
                    var totalWalk = 0
                    var walkCount = 0
                    for (j in 0..jSubPath.length()-1) {
                        val subPath = jSubPath.getJSONObject(j)
                        if (subPath.getInt("trafficType") == 3) { // 도보
                            if(subPath.getInt("sectionTime") > 0) {
                                subPathList.add(SearchPubTransPath.SubPath(subPath.getInt("trafficType"), subPath.getInt("sectionTime"), SearchPubTransPath.Lane(null, null, null, null), "", ""))
                                totalWalk += subPath.getInt("sectionTime")
                                walkCount += 1
                            }
                        }
                        else if (subPath.getInt("trafficType") == 1) // 지하철
                            subPathList.add(SearchPubTransPath.SubPath(subPath.getInt("trafficType"), subPath.getInt("sectionTime"), SearchPubTransPath.Lane(subPath.getJSONArray("lane").getJSONObject(0).getString("name"), null, null, subPath.getJSONArray("lane").getJSONObject(0).getInt("subwayCode")), subPath.getString("startName"), subPath.getString("endName")))
                        else if (subPath.getInt("trafficType") == 2) // 버스
                            subPathList.add(SearchPubTransPath.SubPath(subPath.getInt("trafficType"), subPath.getInt("sectionTime"), SearchPubTransPath.Lane(null, subPath.getJSONArray("lane").getJSONObject(0).getString("busNo"), subPath.getJSONArray("lane").getJSONObject(0).getInt("type"), null), subPath.getString("startName"), subPath.getString("endName")))
                    }
                    val transPath = SearchPubTransPath(jObject.getInt("pathType"), SearchPubTransPath.Path(totalWalk, jInfo.getInt("totalTime"), jInfo.getInt("payment"), walkCount), subPathList)
                    transPathList.add(transPath)
                }

                //directionRecyclerAdapter.dataChange(transPathList)
                directionRecyclerAdapter = DirectionRecyclerAdapter(this, windowManager.defaultDisplay, transPathList)
                binding.recyclerviewDirection.adapter = directionRecyclerAdapter
                binding.layoutDirection.visibility = View.VISIBLE
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


