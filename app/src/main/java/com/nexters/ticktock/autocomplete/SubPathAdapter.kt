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
        if(subPathList[i].trafficType == 1) binding.tvTransportNo.text = subPathList[i].lane.name // 지하철
        else if(subPathList[i].trafficType == 2) binding.tvTransportNo.text = subPathList[i].lane.busNo // 버스

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