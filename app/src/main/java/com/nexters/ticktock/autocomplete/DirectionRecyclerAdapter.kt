package com.nexters.ticktock.autocomplete

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ItemDirectionBinding
import java.text.DecimalFormat

class DirectionRecyclerAdapter(val context: Context?, val display: Display): RecyclerView.Adapter<DirectionRecyclerAdapter.DirectionViewHolder>() {

    private lateinit var binding: ItemDirectionBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DirectionViewHolder {
        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_direction, viewGroup, false)

        val directionHolder = DirectionViewHolder(binding)
        return directionHolder
    }

    override fun getItemCount(): Int {
        // TODO SearchPubTransPath 리스트 사이즈
        return 5
    }

    fun getWidth(): Int {
        val point = Point()
        display.getSize(point)
        return point.x
    }

    override fun onBindViewHolder(holder: DirectionViewHolder, position: Int) {
        // TODO 데이터 삽입 SearchPubTransPath 리스트
        val subPathList = ArrayList<SearchPubTransPath.SubPath>()
        subPathList.add(SearchPubTransPath.SubPath(3, 5, SearchPubTransPath.Lane(null, null), "", ""))
        subPathList.add(SearchPubTransPath.SubPath(2, 15, SearchPubTransPath.Lane(null, "7"), "고읍주공4단지", "도봉면허시험장"))
        subPathList.add(SearchPubTransPath.SubPath(1, 60, SearchPubTransPath.Lane("2호선", null), "도봉면허시험장", "신분당선강남역"))
        subPathList.add(SearchPubTransPath.SubPath(3, 5, SearchPubTransPath.Lane(null, null), "", ""))

        val transPath = SearchPubTransPath(3, SearchPubTransPath.Path(10, 85, 1250, 2), subPathList)
        val totalLength = getWidth() - (100 * transPath.path.walkCount)
        val partOfLength = totalLength / (transPath.path.totalTime - transPath.path.totalWalk)

        /*
         * 길찾기 간략 정보 시작
         */
        binding.tvDirectionTotalTime.text = "${transPath.path.totalTime}분"
        binding.tvDirectionWalkTime.text = "도보 ${transPath.path.totalWalk}분"
        val decimal = DecimalFormat("###,###")
        val payment = decimal.format(transPath.path.payment)
        binding.tvDirectionCost.text = "${payment}원"
        /*
         * 종료
         */

        /*
         * 길찾기 바 시작
         */
        for (subpath in transPath.subPathList) {
            val sectionTime = TextView(context)
            val width = partOfLength * subpath.sectionTime

            if(subpath.trafficType == 3) sectionTime.layoutParams = LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT)
            else sectionTime.layoutParams = LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT)

            if(subpath.trafficType == 1) sectionTime.setBackgroundResource(R.color.colorSubway)
            else if(subpath.trafficType == 2) sectionTime.setBackgroundResource(R.color.colorBus)
            else sectionTime.setBackgroundResource(R.color.appMainColor)

            sectionTime.setTextColor(context!!.resources.getColor(R.color.colorWhite))
            sectionTime.gravity = Gravity.CENTER
            sectionTime.textSize = 12f
            sectionTime.text = "${subpath.sectionTime}분"
            binding.linearlayoutBarDirection.addView(sectionTime)
        }
        /*
         * 종료
         */

        /*
         * 길찾기 환승 정보 시작
         */
        // TODO subpath 중 traffictype == 3인 도보를 제거 해야함
        val subPathListExceptWalk = ArrayList<SearchPubTransPath.SubPath>()
        for (subpath in subPathList) {
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