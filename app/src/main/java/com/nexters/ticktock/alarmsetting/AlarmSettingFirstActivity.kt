package com.nexters.ticktock.alarmsetting

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.nexters.ticktock.OrmAppCompatActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.autocomplete.AutoCompleteActivity
import com.nexters.ticktock.autocomplete.GPSInfo
import com.nexters.ticktock.databinding.ActivityAlarmSettingFirstBinding

class AlarmSettingFirstActivity : OrmAppCompatActivity() {

    private val MAIN_ACTIVITY_REQUEST_CODE = 1111
    private val GPS_ENABLE_REQUEST_CODE = 2001

    private lateinit var binding : ActivityAlarmSettingFirstBinding

    private lateinit var gps: GPSInfo // gps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_setting_first)

        gps = GPSInfo(this) // GPS
        gps.isGPSConnected()

        binding.firstSettingDirectionButton.setOnClickListener {
            val intent = Intent(this, AutoCompleteActivity::class.java)
            intent.putExtra("GPS_RESULT", gps.getResult())
            startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_CODE)
        }

        binding.firstSettingBackButton.setOnClickListener {
            finish()
        }

        binding.firstSettingNextButton.setOnClickListener {
            AlarmSettingSecondActivity.editMode = 1
            val intent: Intent = Intent(this, AlarmSettingSecondActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // gps 설정 변경 후 재연결
            GPS_ENABLE_REQUEST_CODE -> gps.getLocation()

            MAIN_ACTIVITY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //binding.firstSettingDirectionButton.text =  "${data?.getIntExtra("totalTime", 0)}분"
                    }
                }
            }
        }
    }
}