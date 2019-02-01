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

class DirectionRecyclerAdapter(val context: Context?, val display: Display, val transPath: ArrayList<SearchPubTransPath>): RecyclerView.Adapter<DirectionRecyclerAdapter.DirectionViewHolder>() {

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
        return point.x
    }

    override fun onBindViewHolder(holder: DirectionViewHolder, i: Int) {

        val totalLength = getWidth() - (100 * transPath[i].path.walkCount)
        val partOfLength = totalLength / (transPath[i].path.totalTime - transPath[i].path.totalWalk)

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
        for (subpath in transPath[i].subPathList) {
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