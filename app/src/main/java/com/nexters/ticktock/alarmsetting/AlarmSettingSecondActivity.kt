package com.nexters.ticktock.alarmsetting

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import com.nexters.ticktock.OrmAppCompatActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityAlarmSettingSecondBinding
import com.nexters.ticktock.timer.TimerActivity

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
            PrepareModel("", 0)
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


        /* TODO: 뒤로가기 버튼 -> 편집 버튼으로 바꾸자 */
        binding.secondSettingBackButton.setOnClickListener {view ->
            editMode = if (editMode == 1) {
                2
            } else {
                1
            }

            adapter.notifyDataSetChanged()
            binding.secondSettingRecycler.adapter = adapter
        }

        // 다음 타이머 엑티비티로 넘김
        binding.secondSettingNextButton.setOnClickListener {view ->
            val intent = Intent(this, AlarmSettingThirdActivity::class.java)
            startActivity(intent)
        }

        /* TODO: warning 없애자 */
        binding.secondSettingRoot.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                binding.secondSettingRoot.isFocusableInTouchMode = true
                binding.secondSettingRoot.requestFocus()
            }

            return@setOnTouchListener false
        }

        binding.secondSettingRecycler.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                binding.secondSettingRoot.isFocusableInTouchMode = true
                binding.secondSettingRoot.requestFocus()
            }

            return@setOnTouchListener false
        }
    }
}