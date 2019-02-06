package com.nexters.ticktock.alarmsetting

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.nexters.ticktock.OrmAppCompatActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityAlarmSettingSecondBinding

class AlarmSettingSecondActivity : OrmAppCompatActivity() {

    lateinit var binding: ActivityAlarmSettingSecondBinding

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
        binding.secondSettingRecycler.adapter = PrepareAdapter(this, prepareList)
    }
}