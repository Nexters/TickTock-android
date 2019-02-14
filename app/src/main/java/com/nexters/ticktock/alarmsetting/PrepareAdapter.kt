package com.nexters.ticktock.alarmsetting

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.nexters.ticktock.R
import kotlinx.android.synthetic.main.activity_alarm_setting_second.view.*
import kotlinx.android.synthetic.main.item_prepare.view.*
import kotlinx.android.synthetic.main.item_prepare_edit.view.*
import java.util.*
import android.view.inputmethod.InputMethodManager


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

    override fun onBindViewHolder(holder: ItemPrepareHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            if (AlarmSettingSecondActivity.editMode == 1)
                holder.bindEditMode(prepareList[position])
            else
                holder.bindChangeMode(prepareList[position])
        } else {
            for (payload in payloads) {
                if (payload.toString() == "timeFocus" || payload.toString() == "timeButton") {
                    holder.bindTimeEditText(prepareList[position])
                } else if (payload.toString() == "nameFocus") {
                    holder.bindNameEditText(prepareList[position])
                }
            }
        }
    }

    inner class ItemPrepareHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindEditMode(item: PrepareModel) {
            itemView.prepare_name_edit.text = Editable.Factory.getInstance().newEditable(item.name)
            itemView.prepare_time_edit.text = Editable.Factory.getInstance().newEditable(item.time.toString().plus("분"))
            itemView.prepare_plus_button.setOnClickListener {
                item.time++
                notifyItemChanged(adapterPosition, "timeButton")
            }
            itemView.prepare_minus_button.setOnClickListener {
                item.time--
                notifyItemChanged(adapterPosition, "timeButton")
            }

            itemView.prepare_name_edit.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus && v.prepare_name_edit.text.toString() != item.name) {
                    item.name = v.prepare_name_edit.text.toString()
                    notifyItemChanged(adapterPosition, "nameFocus")
                }

                if (adapterPosition == prepareList.size -1 && !hasFocus) {
                    if (!itemView.prepare_name_edit.text.isEmpty()) {
                        val model = PrepareModel("", 0)
                        prepareList.add(model)
                        notifyItemInserted(adapterPosition + 1)
                    }
                }
            }

            /* TODO: edit중에 버튼을 클릭하면 에러나는 버그를 고치자 */
            itemView.prepare_time_edit.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    var temp = v.prepare_time_edit.text.toString()

                    temp = temp.substring(0, temp.length - 1)

                    v.prepare_time_edit.text = Editable.Factory.getInstance().newEditable(temp)
                } else {
                    val temp = v.prepare_time_edit.text.toString()

                    prepareList[adapterPosition].time = temp.toInt()
                    notifyItemChanged(adapterPosition, "timeFocus")
                }
            }
        }

        fun bindChangeMode(item: PrepareModel) {
            if (!item.name.isEmpty()) {
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
            } else {
                itemView.visibility = View.INVISIBLE
            }
        }

        /******************** view event 에 사용되는 bind 함수들 *********************/

        fun bindNameEditText(item: PrepareModel) {
            itemView.prepare_name_edit.text = Editable.Factory.getInstance().newEditable(item.name)
            itemView.prepare_name_edit.clearFocus()
        }

        fun bindTimeEditText(item: PrepareModel) {
            itemView.prepare_time_edit.text = Editable.Factory.getInstance().newEditable(item.time.toString().plus("분"))
        }
    }
}

