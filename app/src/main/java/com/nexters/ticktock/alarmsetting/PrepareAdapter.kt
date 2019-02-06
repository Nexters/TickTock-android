package com.nexters.ticktock.alarmsetting

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nexters.ticktock.R


class PrepareAdapter(val context: Context, var prepareList: ArrayList<PrepareModel>) : RecyclerView.Adapter<PrepareAdapter.ItemPrepareHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemPrepareHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_prepare, parent, false)
        return ItemPrepareHolder(view)
    }

    override fun getItemCount(): Int {
        return prepareList.size
    }

    override fun onBindViewHolder(holder: ItemPrepareHolder, position: Int) {
        holder.bind(prepareList[position])
    }

    inner class ItemPrepareHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: PrepareModel) {
            itemView.findViewById<TextView>(R.id.prepare_name_text).text = item.name
            itemView.findViewById<TextView>(R.id.prepare_time_text).text = item.time.toString().plus("분")
        }
    }
}