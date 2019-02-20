package com.nexters.ticktock.autocomplete

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ItemSubpathBinding

class SubPathAdapter(val context: Context?, val subPathList: ArrayList<SearchPubTransPath.SubPath>): RecyclerView.Adapter<SubPathAdapter.SubPathViewHolder>() {

    private lateinit var binding: ItemSubpathBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SubPathViewHolder {
        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_subpath, viewGroup, false)

        val subPathHolder = SubPathViewHolder(binding)
        return subPathHolder
    }

    override fun getItemCount(): Int {
        return subPathList.size
    }

    override fun onBindViewHolder(holder: SubPathViewHolder, i: Int) {
        if(subPathList[i].trafficType == 1) { // 지하철
            binding.tvTransportNo.visibility = View.GONE

            when (subPathList[i].lane.subwayCode) {
                1 -> binding.ivImage.setImageResource(R.drawable.icn_subway_1)
                2 -> binding.ivImage.setImageResource(R.drawable.icn_subway_2)
                3 -> binding.ivImage.setImageResource(R.drawable.icn_subway_3)
                4 -> binding.ivImage.setImageResource(R.drawable.icn_subway_4)
                5 -> binding.ivImage.setImageResource(R.drawable.icn_subway_5)
                6 -> binding.ivImage.setImageResource(R.drawable.icn_subway_6)
                7 -> binding.ivImage.setImageResource(R.drawable.icn_subway_7)
                8 -> binding.ivImage.setImageResource(R.drawable.icn_subway_8)
                9 -> binding.ivImage.setImageResource(R.drawable.icn_subway_9)
                100 -> binding.ivImage.setImageResource(R.drawable.icn_subway_bundang)
                101 -> binding.ivImage.setImageResource(R.drawable.icn_subway_airport)
                102 -> binding.ivImage.setImageResource(R.drawable.icn_subway_zaki)
                104 -> binding.ivImage.setImageResource(R.drawable.icn_subway_gyeong_ui)
                107 -> binding.ivImage.setImageResource(R.drawable.icn_subway_ever)
                108 -> binding.ivImage.setImageResource(R.drawable.icn_subway_gyeong_chun)
                109 -> binding.ivImage.setImageResource(R.drawable.icn_subway_sin)
                110 -> binding.ivImage.setImageResource(R.drawable.icn_subway_uijeong)
                111 -> binding.ivImage.setImageResource(R.drawable.icn_subway_suin)
                112 -> binding.ivImage.setImageResource(R.drawable.icn_subway_gyeonggang)
                113 -> binding.ivImage.setImageResource(R.drawable.icn_subway_wui)
                114 -> binding.ivImage.setImageResource(R.drawable.icn_subway_seohae)
                21 -> binding.ivImage.setImageResource(R.drawable.icn_subway_in_1)
                22 -> binding.ivImage.setImageResource(R.drawable.icn_subway_in_2)
                31 -> binding.ivImage.setImageResource(R.drawable.icn_subway_daejeon_1)
                41 -> binding.ivImage.setImageResource(R.drawable.icn_subway_dae_1)
                42 -> binding.ivImage.setImageResource(R.drawable.icn_subway_dae_2)
                43 -> binding.ivImage.setImageResource(R.drawable.icn_subway_dae_3)
                51 -> binding.ivImage.setImageResource(R.drawable.icn_subway_gwangju)
                71 -> binding.ivImage.setImageResource(R.drawable.icn_subway_busan_1)
                72 -> binding.ivImage.setImageResource(R.drawable.icn_subway_busan_2)
                73 -> binding.ivImage.setImageResource(R.drawable.icn_subway_busan_3)
                74 -> binding.ivImage.setImageResource(R.drawable.icn_subway_busan_4)
                78 -> binding.ivImage.setImageResource(R.drawable.icn_subway_donghae)
                79 -> binding.ivImage.setImageResource(R.drawable.icn_subway_bukim)
                else -> binding.ivImage.setImageResource(R.drawable.icn_subway_1)
            }
        }
        else if(subPathList[i].trafficType == 2) { // 버스
            binding.tvTransportNo.text = subPathList[i].lane.busNo

            when (subPathList[i].lane.type) {
                1 -> binding.ivImage.setImageResource(R.drawable.icn_bus_normal)
                3 -> binding.ivImage.setImageResource(R.drawable.icn_bus_village)
                5 -> binding.ivImage.setImageResource(R.drawable.icn_bus_airport)
                6 -> binding.ivImage.setImageResource(R.drawable.icn_bus_line)
                11 -> binding.ivImage.setImageResource(R.drawable.icn_bus_line)
                14 -> binding.ivImage.setImageResource(R.drawable.icn_bus_wide)
                26 -> binding.ivImage.setImageResource(R.drawable.icn_bus_line)
                else -> binding.ivImage.setImageResource(R.drawable.icn_bus_normal)
            }
        }

        binding.tvStartName.text = subPathList[i].startName
        if (i == 0)
            binding.tvSubpathHow.text = "승차"
        else
            binding.tvSubpathHow.text = "환승"

        if(i == subPathList.size - 1) { // 마지막 인덱스
            binding.layoutEnd.visibility = View.VISIBLE
            binding.tvEndName.text = subPathList[i].endName
        }
    }

    class SubPathViewHolder(binding: ItemSubpathBinding): RecyclerView.ViewHolder(binding.root)
}