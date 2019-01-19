package com.nexters.ticktock

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener, OnResultCallbackListener {
    lateinit var odsayService: ODsayService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService = ODsayService.init(this, getResources().getString(R.string.odsay_key));
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                newActivity()
                // odsayService.requestSearchPubTransPath("126.926493082645", "37.6134436427887", "127.126936754911", "37.5004198786564", "0", "0", "0", this)
            }
        }
    }

    fun newActivity() {
        val intent = Intent(this, AutoCompleteActivity::class.java)
        startActivity(intent)
    }

    override fun onSuccess(odsayData: ODsayData, api: API?) {
        try {
            // API Value 는 API 호출 메소드 명을 따라갑니다.
            if (api == API.SEARCH_PUB_TRANS_PATH) {
                val jArray:JSONArray = odsayData.getJson().getJSONObject("result").getJSONArray("path")
                var jObject:JSONObject = jArray.getJSONObject(0).getJSONObject("info") // 최단시간
                var totalTime:Int = jObject.getInt("totalTime")

                Log.d("totalTime : %s", "${totalTime}");
            }
        }catch (e: JSONException) {
            e.printStackTrace();
        }
    }

    override fun onError(i: Int, s: String?, api: API?) {
        if (api == API.BUS_STATION_INFO) {}
    }
}
