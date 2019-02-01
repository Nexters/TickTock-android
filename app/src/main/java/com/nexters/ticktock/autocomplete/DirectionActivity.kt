package com.nexters.ticktock.autocomplete

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityDirectionBinding
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import org.json.JSONArray
import org.json.JSONException

// TODO 임시적용
class DirectionActivity : AppCompatActivity(), OnResultCallbackListener {

    private lateinit var binding: ActivityDirectionBinding

    private lateinit var odsayService: ODsayService // 오디세이

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direction)

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService = ODsayService.init(this, getResources().getString(R.string.odsay_key))
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000)
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000)

        odsayService.requestSearchPubTransPath(
                "127.0770329",
                "37.7982884",
                "127.0329199",
                "37.4968436",
                "0",
                "0",
                "0",
                this)
    }

    /*
     * 오디세이 api 사용을 위한 메소드 시작
     */
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
                                subPathList.add(SearchPubTransPath.SubPath(subPath.getInt("trafficType"), subPath.getInt("sectionTime"), SearchPubTransPath.Lane(null, null), "", ""))
                                totalWalk += subPath.getInt("sectionTime")
                                walkCount += 1
                            }
                        }
                        else if (subPath.getInt("trafficType") == 1) // 지하철
                            subPathList.add(SearchPubTransPath.SubPath(subPath.getInt("trafficType"), subPath.getInt("sectionTime"), SearchPubTransPath.Lane(subPath.getJSONArray("lane").getJSONObject(0).getString("name"), null), subPath.getString("startName"), subPath.getString("endName")))
                        else if (subPath.getInt("trafficType") == 2) // 버스
                            subPathList.add(SearchPubTransPath.SubPath(subPath.getInt("trafficType"), subPath.getInt("sectionTime"), SearchPubTransPath.Lane(null, subPath.getJSONArray("lane").getJSONObject(0).getString("busNo")), subPath.getString("startName"), subPath.getString("endName")))
                    }
                    val transPath = SearchPubTransPath(jObject.getInt("pathType"), SearchPubTransPath.Path(totalWalk, jInfo.getInt("totalTime"), jInfo.getInt("payment"), walkCount), subPathList)
                    transPathList.add(transPath)
                }
                val directionPagerAdapter = DirectionPagerAdapter(supportFragmentManager, transPathList)
                binding.viewpagerDirection.adapter = directionPagerAdapter
                Log.d("TAG", jArray.toString())
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
