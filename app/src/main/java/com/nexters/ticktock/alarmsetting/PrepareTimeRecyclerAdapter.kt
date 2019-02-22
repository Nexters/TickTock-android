package com.nexters.ticktock.alarmsetting

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ItemPrepareTimeBinding
import com.nexters.ticktock.model.Step

class PrepareTimeRecyclerAdapter (
        private val context: Context?,
        private val stepList: ArrayList<Step>
): RecyclerView.Adapter<PrepareTimeRecyclerAdapter.PrepareTimeViewHolder>() {

    private lateinit var binding: ItemPrepareTimeBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PrepareTimeViewHolder {
        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_prepare_time, viewGroup, false)

        val prepareTimeHolder = PrepareTimeViewHolder(binding)
        return prepareTimeHolder
    }

    override fun getItemCount(): Int {
        return stepList.size
    }

    override fun onBindViewHolder(holder: PrepareTimeViewHolder, i: Int) {
        binding.tvTitle.text = stepList.get(i).name
        binding.tvDescription.text = "${stepList.get(i).duration.time}분"
    }

    class PrepareTimeViewHolder(val binding: ItemPrepareTimeBinding): RecyclerView.ViewHolder(binding.root)
}