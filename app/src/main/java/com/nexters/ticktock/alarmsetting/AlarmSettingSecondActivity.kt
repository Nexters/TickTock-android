package com.nexters.ticktock.alarmsetting

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Toast
import com.nexters.ticktock.OrmAppCompatActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityAlarmSettingSecondBinding

class AlarmSettingSecondActivity : OrmAppCompatActivity(), PrepareAdapter.OnStartDragListener {

    companion object {
        var editMode : Int = 1
    }

    lateinit var binding: ActivityAlarmSettingSecondBinding
    lateinit var itemTouchHelper: ItemTouchHelper

    override fun onStartDrag(itemPrepareHolder: PrepareAdapter.ItemPrepareHolder) {
        itemTouchHelper.startDrag(itemPrepareHolder)
    }

    var prepareList: ArrayList<PrepareModel> = arrayListOf(
            PrepareModel("데이트", 1),
            PrepareModel("출근", 2),
            PrepareModel("씻기", 3),
            PrepareModel("출근", 2),
            PrepareModel("출근", 2),
            PrepareModel("출근", 2),
            PrepareModel("출근", 2),
            PrepareModel("출근", 2),
            PrepareModel("출근", 2),
            PrepareModel("출근", 2),
            PrepareModel("출근", 2)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_setting_second)

        binding.secondSettingRecycler.layoutManager = LinearLayoutManager(this)

        val adapter = PrepareAdapter(this, prepareList, this)

        val itemTouchHelperCallback = PrepareItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.secondSettingRecycler)

        binding.secondSettingRecycler.adapter = adapter

        /* TODO: 다음버튼 -> 편집 버튼으로 바꾸자 */
        binding.secondSettingNextButton.setOnClickListener {view ->
            editMode = if (editMode == 1) {
                2
            } else {
                1
            }
            
            adapter.notifyDataSetChanged()
            binding.secondSettingRecycler.adapter = adapter
        }
    }
}