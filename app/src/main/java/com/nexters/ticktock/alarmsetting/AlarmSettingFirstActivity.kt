package com.nexters.ticktock.alarmsetting

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.nexters.ticktock.OrmAppCompatActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityAlarmSettingFirstBinding

class AlarmSettingFirstActivity : OrmAppCompatActivity() {

    private lateinit var binding : ActivityAlarmSettingFirstBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_setting_first)
    }
}