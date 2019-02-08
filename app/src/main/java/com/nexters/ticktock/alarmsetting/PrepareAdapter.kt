package com.nexters.ticktock.alarmsetting

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.nexters.ticktock.R
import kotlinx.android.synthetic.main.item_prepare.view.*
import kotlinx.android.synthetic.main.item_prepare_edit.view.*
import java.util.*


class PrepareAdapter(val context: Context, var prepareList: ArrayList<PrepareModel>, val startDragListener: OnStartDragListener) : RecyclerView.Adapter<PrepareAdapter.ItemPrepareHolder>(), PrepareItemTouchHelperCallback.OnItemMoveListener {

    interface OnStartDragListener {
        fun onStartDrag(itemPrepareHolder: ItemPrepareHolder)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(prepareList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemPrepareHolder {
        val editView = LayoutInflater.from(context).inflate(R.layout.item_prepare_edit, parent, false)
        val changeView = LayoutInflater.from(context).inflate(R.layout.item_prepare, parent, false)

        return if (AlarmSettingSecondActivity.editMode == 1)
            ItemPrepareHolder(editView)
        else
            ItemPrepareHolder(changeView)
    }

    override fun getItemCount(): Int {
        return prepareList.size
    }

    override fun onBindViewHolder(holder: ItemPrepareHolder, position: Int) {
        if (AlarmSettingSecondActivity.editMode == 1)
            holder.bindEditMode(prepareList[position])
        else
            holder.bindChangeMode(prepareList[position])
    }

    inner class ItemPrepareHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindEditMode(item: PrepareModel) {
            itemView.prepare_name_edit.text = Editable.Factory.getInstance().newEditable(item.name)
            itemView.prepare_time_edit.text = Editable.Factory.getInstance().newEditable(item.time.toString().plus("분"))
            itemView.prepare_plus_button.setOnClickListener {
                item.time++
                notifyDataSetChanged()
            }
        }

        fun bindChangeMode(item: PrepareModel) {
            itemView.prepare_name_text.text = item.name
            itemView.prepare_time_text.text = item.time.toString().plus("분")
            itemView.list_recycle.setOnTouchListener { _, _ ->
                prepareList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)

                return@setOnTouchListener false
            }
            itemView.list_order_change.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    startDragListener.onStartDrag(this)
                }
                return@setOnTouchListener false
            }
        }
    }
}

