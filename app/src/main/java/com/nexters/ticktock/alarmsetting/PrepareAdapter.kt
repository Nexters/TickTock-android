package com.nexters.ticktock.alarmsetting

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityAlarmSettingSecondBinding
import kotlinx.android.synthetic.main.activity_alarm_setting_second.view.*
import kotlinx.android.synthetic.main.item_prepare.view.*
import kotlinx.android.synthetic.main.item_prepare_edit.view.*
import java.util.*


class PrepareAdapter(val context: Context, var prepareList: ArrayList<PrepareModel>, val startDragListener: OnStartDragListener) : RecyclerView.Adapter<PrepareAdapter.ItemPrepareHolder>(), PrepareItemTouchHelperCallback.OnItemMoveListener {

    lateinit var binding: ActivityAlarmSettingSecondBinding

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

        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_alarm_setting_second, parent, false)

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
            if (adapterPosition == prepareList.size - 1) {
                itemView.prepare_name_edit.visibility = View.INVISIBLE
                itemView.prepare_time_edit.visibility = View.INVISIBLE
                itemView.prepare_plus_button.visibility = View.INVISIBLE
                itemView.prepare_minus_button.visibility = View.INVISIBLE

                itemView.prepare_item_plus_button.visibility = View.VISIBLE

                itemView.prepare_item_plus_button.setOnClickListener { view ->
                    val model = PrepareModel("", 1)
                    prepareList.add(model)
                    notifyItemInserted(adapterPosition)

                    if (prepareList.size == 1) {
                        binding.secondSettingNextButton.isEnabled = false
                        binding.secondSettingNextButton.setBackgroundColor(Color.parseColor("#D8D8D8"))
                    } else {
                        binding.secondSettingNextButton.isEnabled = true
                        binding.secondSettingNextButton.setBackgroundColor(Color.parseColor("#f6460f"))
                    }
                }
            } else {
                if (!item.name.isEmpty()) {
                    itemView.prepare_plus_button.setImageResource(R.drawable.btn_min_plus_nor)
                    itemView.prepare_minus_button.setImageResource(R.drawable.btn_min_minor_nor)
                }

                itemView.prepare_name_edit.text = Editable.Factory.getInstance().newEditable(item.name)
                itemView.prepare_time_edit.text = Editable.Factory.getInstance().newEditable(item.time.toString().plus("분"))
                itemView.prepare_plus_button.setOnClickListener {
                    if (!itemView.prepare_name_edit.text.isEmpty()) {
                        item.time++
                        notifyItemChanged(adapterPosition, "timeButton")
                    }
                }
                itemView.prepare_minus_button.setOnClickListener {
                    if (!itemView.prepare_name_edit.text.isEmpty()) {
                        item.time--
                        notifyItemChanged(adapterPosition, "timeButton")
                    }
                }

                itemView.prepare_name_edit.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (!itemView.prepare_name_edit.text.isEmpty()) {
                            itemView.prepare_plus_button.setImageResource(R.drawable.btn_min_plus_nor)
                            itemView.prepare_minus_button.setImageResource(R.drawable.btn_min_minor_nor)
                        } else {
                            itemView.prepare_plus_button.setImageResource(R.drawable.btn_min_plus_dis)
                            itemView.prepare_minus_button.setImageResource(R.drawable.btn_min_minor_dis)
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        Log.d("", "")
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        Log.d("", "")
                    }
                })

                itemView.prepare_name_edit.setOnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus && v.prepare_name_edit.text.toString() != item.name) {
                        item.name = v.prepare_name_edit.text.toString()
                        notifyItemChanged(adapterPosition, "nameFocus")
                    }

                    if (!hasFocus && v.prepare_name_edit.text.isEmpty()) {
                        prepareList.remove(item)
                        notifyItemRemoved(adapterPosition)
                    }
                }

                itemView.prepare_time_edit.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        var temp = v.prepare_time_edit.text.toString()

                        temp = temp.replace("분", "")

                        v.prepare_time_edit.text = Editable.Factory.getInstance().newEditable(temp)
                    } else {
                        var tempInt = if (v.prepare_time_edit.text.toString() == "") {
                            1
                        } else {
                            v.prepare_time_edit.text.toString().toInt()
                        }

                        if (tempInt > 99) {
                            tempInt = 99
                        } else if (tempInt < 1) {
                            tempInt = 1
                        }

                        var temp = tempInt.toString()

                        temp = temp.replace("분", "")

                        if (!temp.isEmpty())
                            prepareList[layoutPosition].time = temp.toInt()
                        else
                            prepareList[layoutPosition].time = 1

                        notifyItemChanged(layoutPosition, "timeFocus")
                    }
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

