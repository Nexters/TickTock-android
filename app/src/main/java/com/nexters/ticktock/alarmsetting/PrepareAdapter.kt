package com.nexters.ticktock.alarmsetting

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nexters.ticktock.R
import java.util.*


class PrepareAdapter(val context: Context, var prepareList: ArrayList<PrepareModel>, val startDragListener: OnStartDragListener) : RecyclerView.Adapter<PrepareAdapter.ItemPrepareHolder>(), PrepareItemTouchHelperCallback.OnItemMoveListener {

    interface OnStartDragListener {
        fun onStartDrag(itemPrepareHolder:ItemPrepareHolder)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(prepareList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        return true
    }

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



        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: PrepareModel) {
            itemView.findViewById<TextView>(R.id.prepare_name_text).text = item.name
            itemView.findViewById<TextView>(R.id.prepare_time_text).text = item.time.toString().plus("분")
            itemView.findViewById<ImageView>(R.id.list_order_change).setOnTouchListener {
                view, event -> if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                startDragListener.onStartDrag(this)
            }
                return@setOnTouchListener false
            }
        }
    }
}