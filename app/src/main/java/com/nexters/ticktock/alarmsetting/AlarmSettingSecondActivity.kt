package com.nexters.ticktock.alarmsetting

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.nexters.ticktock.R
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.databinding.ActivityAlarmSettingSecondBinding
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.model.Step
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.utils.Time
import java.util.*

class AlarmSettingSecondActivity : AppCompatActivity(), PrepareAdapter.OnStartDragListener {

    companion object {
        var editMode : Int = 1
    }

    lateinit var binding: ActivityAlarmSettingSecondBinding
    lateinit var itemTouchHelper: ItemTouchHelper

    private lateinit var daySet: EnumSet<Day>
    private var startLocation: String? = null
    private var endLocation: String? = null
    private var travelTime: Int? = null

    override fun onStartDrag(itemPrepareHolder: PrepareAdapter.ItemPrepareHolder) {
        itemTouchHelper.startDrag(itemPrepareHolder)
    }

    var prepareList: ArrayList<PrepareModel> = arrayListOf(
            PrepareModel("", 0)
    )

    private fun getData() {
        daySet = intent.getSerializableExtra("daySet") as EnumSet<Day>
        startLocation = intent.getStringExtra("startLocation")
        endLocation = intent.getStringExtra("endLocation")
        travelTime = intent.getIntExtra("travelTime", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_setting_second)

        binding.secondSettingRecycler.layoutManager = LinearLayoutManager(this)

        val adapter = PrepareAdapter(this, prepareList, this)

        val itemTouchHelperCallback = PrepareItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.secondSettingRecycler)

        val prepareItemDecoration = PrepareItemDecoration(24, 10, this)
        binding.secondSettingRecycler.addItemDecoration(prepareItemDecoration)

        binding.secondSettingRecycler.adapter = adapter

        binding.secondSettingBackButton.setOnClickListener {
            finish()
        }

        binding.secondSettingEditButton.setOnClickListener {
            if (editMode == 1) {
                editMode = 2
                binding.secondSettingEditButton.visibility = View.INVISIBLE
                binding.secondSettingNextImage.visibility = View.INVISIBLE
                binding.secondSettingSaveImage.visibility = View.VISIBLE
                binding.secondSettingCourse.visibility = View.GONE
            } else {
                editMode = 1
            }

            adapter.notifyDataSetChanged()
            binding.secondSettingRecycler.adapter = adapter
        }

        // 다음 타이머 엑티비티로 넘김
        binding.secondSettingNextButton.setOnClickListener {
            if (editMode == 1) {

                var index = 0

                val stepList: ArrayList<Step> = arrayListOf()

                for (item in prepareList) {
                    if (!item.name.isEmpty()) {
                        stepList.add(Step(
                                name = item.name,
                                duration = Time(item.time),
                                order = index++
                        ))
                    }
                }

                val intent = Intent(this, AlarmSettingThirdActivity::class.java)
                intent.putExtra("daySet", daySet)
                intent.putExtra("startLocation", startLocation)
                intent.putExtra("endLocation", endLocation)
                intent.putExtra("travelTime", travelTime)
                intent.putExtra("stepList", stepList)
                startActivity(intent)
            } else if (editMode == 2) {
                editMode = 1
                binding.secondSettingEditButton.visibility = View.VISIBLE
                binding.secondSettingNextImage.visibility = View.VISIBLE
                binding.secondSettingSaveImage.visibility = View.INVISIBLE
                binding.secondSettingCourse.visibility = View.VISIBLE

                adapter.notifyDataSetChanged()
                binding.secondSettingRecycler.adapter = adapter
            }
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

        binding.secondSettingScroll.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                binding.secondSettingRoot.isFocusableInTouchMode = true
                binding.secondSettingRoot.requestFocus()
            }

            return@setOnTouchListener false
        }
    }
}