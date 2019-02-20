package com.nexters.ticktock.autocomplete

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ItemDirectionBinding
import java.text.DecimalFormat

class DirectionRecyclerAdapter(
        val context: Context?,
        val display: Display,
        var transPath: ArrayList<SearchPubTransPath>
): RecyclerView.Adapter<DirectionRecyclerAdapter.DirectionViewHolder>() {

    private lateinit var binding: ItemDirectionBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DirectionViewHolder {
        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_direction, viewGroup, false)

        val directionHolder = DirectionViewHolder(binding)
        return directionHolder
    }

    override fun getItemCount(): Int {
        return transPath.size
    }

    fun getWidth(): Int {
        val point = Point()
        display.getSize(point)
        return point.x - 90
    }

    fun dataChange(tp: ArrayList<SearchPubTransPath>) {
        transPath.clear()
        transPath.addAll(tp)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DirectionViewHolder, i: Int) {

        val totalLength = getWidth() - 80 * transPath[i].subPathList.size
        val partOfLength = totalLength.toDouble() / transPath[i].path.totalTime.toDouble()

        /*
         * 길찾기 간략 정보 시작
         */
        binding.tvDirectionTotalTime.text = "${transPath[i].path.totalTime}분"
        binding.tvDirectionWalkTime.text = "도보 ${transPath[i].path.totalWalk}분"
        val decimal = DecimalFormat("###,###")
        val payment = decimal.format(transPath[i].path.payment)
        binding.tvDirectionCost.text = "${payment}원"
        /*
         * 종료
         */

        /*
         * 길찾기 바 시작
         */

        for (j in transPath[i].subPathList.indices) {
            val sectionTime = TextView(context)
            var width = partOfLength * transPath[i].subPathList[j].sectionTime
            width -= width / 10.0
            sectionTime.layoutParams = LinearLayout.LayoutParams(80 + width.toInt(), LinearLayout.LayoutParams.MATCH_PARENT)

            if(transPath[i].subPathList[j].trafficType == 1) {
                when (transPath[i].subPathList[j].lane.subwayCode) {
                    1 -> sectionTime.setBackgroundResource(R.color.subway_line1)
                    2 -> sectionTime.setBackgroundResource(R.color.subway_line2)
                    3 -> sectionTime.setBackgroundResource(R.color.subway_line3)
                    4 -> sectionTime.setBackgroundResource(R.color.subway_line4)
                    5 -> sectionTime.setBackgroundResource(R.color.subway_line5)
                    6 -> sectionTime.setBackgroundResource(R.color.subway_line6)
                    7 -> sectionTime.setBackgroundResource(R.color.subway_line7)
                    8 -> sectionTime.setBackgroundResource(R.color.subway_line8)
                    9 -> sectionTime.setBackgroundResource(R.color.subway_line9)
                    100 -> sectionTime.setBackgroundResource(R.color.subway_bundang)
                    101 -> sectionTime.setBackgroundResource(R.color.subway_airport)
                    102 -> sectionTime.setBackgroundResource(R.color.subway_jagi)
                    104 -> sectionTime.setBackgroundResource(R.color.subway_gyeongui)
                    107 -> sectionTime.setBackgroundResource(R.color.subway_everline)
                    108 -> sectionTime.setBackgroundResource(R.color.subway_gyeongchoon)
                    109 -> sectionTime.setBackgroundResource(R.color.subway_shinbundang)
                    110 -> sectionTime.setBackgroundResource(R.color.subway_uijeongbu)
                    111 -> sectionTime.setBackgroundResource(R.color.subway_suin)
                    112 -> sectionTime.setBackgroundResource(R.color.subway_geonggang)
                    113 -> sectionTime.setBackgroundResource(R.color.subway_wooee)
                    114 -> sectionTime.setBackgroundResource(R.color.subway_seohae)
                    21 -> sectionTime.setBackgroundResource(R.color.subway_incheon1)
                    22 -> sectionTime.setBackgroundResource(R.color.subway_incheon2)
                    31 -> sectionTime.setBackgroundResource(R.color.subway_daejeon1)
                    41 -> sectionTime.setBackgroundResource(R.color.subway_daegu1)
                    42 -> sectionTime.setBackgroundResource(R.color.subway_daegu2)
                    43 -> sectionTime.setBackgroundResource(R.color.subway_daegu3)
                    51 -> sectionTime.setBackgroundResource(R.color.subway_gwangju1)
                    71 -> sectionTime.setBackgroundResource(R.color.subway_busan1)
                    72 -> sectionTime.setBackgroundResource(R.color.subway_busan2)
                    73 -> sectionTime.setBackgroundResource(R.color.subway_busan3)
                    74 -> sectionTime.setBackgroundResource(R.color.subway_busan4)
                    78 -> sectionTime.setBackgroundResource(R.color.subway_east)
                    79 -> sectionTime.setBackgroundResource(R.color.subway_gimhae)
                    else -> sectionTime.setBackgroundResource(R.color.subway_line1)
                }

                sectionTime.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
                sectionTime.gravity = Gravity.CENTER
                sectionTime.textSize = 12f
                sectionTime.text = "${transPath[i].subPathList[j].sectionTime}분"
            } else if(transPath[i].subPathList[j].trafficType == 2) {
                when (transPath[i].subPathList[j].lane.type) {
                    1 -> sectionTime.setBackgroundResource(R.color.bus_normal)
                    3 -> sectionTime.setBackgroundResource(R.color.bus_maeul)
                    5 -> sectionTime.setBackgroundResource(R.color.bus_airport)
                    6 -> sectionTime.setBackgroundResource(R.color.bus_gansun)
                    11 -> sectionTime.setBackgroundResource(R.color.bus_gansun)
                    14 -> sectionTime.setBackgroundResource(R.color.bus_gwangyeok)
                    26 -> sectionTime.setBackgroundResource(R.color.bus_gansun)
                    else -> sectionTime.setBackgroundResource(R.color.bus_normal)
                }

                sectionTime.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
                sectionTime.gravity = Gravity.CENTER
                sectionTime.textSize = 12f
                sectionTime.text = "${transPath[i].subPathList[j].sectionTime}분"
            } else {
                sectionTime.setBackgroundResource(R.color.walk)

                sectionTime.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
                sectionTime.gravity = Gravity.CENTER
                sectionTime.textSize = 12f
                sectionTime.text = "${transPath[i].subPathList[j].sectionTime}분"
            }
            binding.linearlayoutBarDirection.addView(sectionTime)
        }
        /*
         * 종료
         */

        /*
         * 길찾기 환승 정보 시작
         */
        val subPathListExceptWalk = ArrayList<SearchPubTransPath.SubPath>()
        for (subpath in transPath[i].subPathList) {
            if (subpath.trafficType != 3)
                subPathListExceptWalk.add(subpath)
        }
        binding.recyclerviewSubpath.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewSubpath.adapter = SubPathAdapter(context, subPathListExceptWalk)
        /*
         * 종료
         */
    }

    class DirectionViewHolder(binding: ItemDirectionBinding): RecyclerView.ViewHolder(binding.root)
}