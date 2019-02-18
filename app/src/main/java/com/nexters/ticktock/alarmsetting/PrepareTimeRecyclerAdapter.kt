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

class PrepareTimeRecyclerAdapter (
        private val context: Context?,
        private val arrayList: ArrayList<String>
): RecyclerView.Adapter<PrepareTimeRecyclerAdapter.PrepareTimeViewHolder>() {

    private lateinit var binding: ItemPrepareTimeBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PrepareTimeViewHolder {
        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_prepare_time, viewGroup, false)

        val prepareTimeHolder = PrepareTimeViewHolder(binding)
        return prepareTimeHolder
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: PrepareTimeViewHolder, i: Int) {
        // binding.tvTitle
        // binding.tvDescription
    }

    class PrepareTimeViewHolder(val binding: ItemPrepareTimeBinding): RecyclerView.ViewHolder(binding.root)
}