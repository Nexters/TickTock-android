package com.nexters.ticktock

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import com.nexters.ticktock.Constant.GPS_ENABLE_REQUEST_CODE
import com.nexters.ticktock.Constant.MAIN_DETAIL_ACTIVITY_REQUEST_CODE
import com.nexters.ticktock.alarmsetting.AlarmSettingSecondActivity
import com.nexters.ticktock.autocomplete.AutoCompleteActivity
import com.nexters.ticktock.autocomplete.GPSInfo
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.databinding.ActivityMainDetailBinding
import com.nexters.ticktock.utils.getResizedString
import com.nexters.ticktock.utils.getUnderlinedString

class MainDetailActivity(): AppCompatActivity(), View.OnClickListener {

    constructor(id: Long): this() {
        val alarmDao = TickTockDBHelper.getInstance(this).alarmDao
        val alarm = alarmDao.findById(id)
    }

    private lateinit var binding: ActivityMainDetailBinding

    private lateinit var gps: GPSInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_detail)

        gps = GPSInfo(this) // GPS
        gps.isGPSConnected()

        binding.tvAlarmTime.text = getResizedString("*11:30* AM", 3.125f)
        binding.tvDeliveryFromTitle.text = getUnderlinedString("*현위치*")
        binding.tvDeliveryToTitle.text = getUnderlinedString("*종각역 마이크임팩트*")

        binding.btnClose.setOnClickListener(this)
        binding.btnEdit.setOnClickListener(this)
        binding.btnEditPrepareTime.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

        binding.tpMeetingTime.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener{
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                // TODO data set
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // gps 설정 변경 후 재연결
            GPS_ENABLE_REQUEST_CODE -> gps.getLocation()

            MAIN_DETAIL_ACTIVITY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //binding.firstSettingDirectionButton.text =  "${data?.getIntExtra("totalTime", 0)}분"
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnClose.id -> finish()

            binding.btnEdit.id -> {
                val intent = Intent(this, AutoCompleteActivity::class.java)
                intent.putExtra("GPS_RESULT", gps.getResult())
                intent.putExtra("DESTINATION_DATA", binding.tvDeliveryToTitle.text)
                startActivityForResult(intent, MAIN_DETAIL_ACTIVITY_REQUEST_CODE)
            }

            binding.btnEditPrepareTime.id -> {
                val intent = Intent(this, AlarmSettingSecondActivity::class.java)
                startActivity(intent)
            }

            binding.btnSave.id -> {
                // TODO save
                //finish()
            }
        }
    }

    fun setData() {

        binding.cbMonday.isChecked
        binding.cbTuesday.isChecked
        binding.cbWednesday.isChecked
        binding.cbThursday.isChecked
        binding.cbFriday.isChecked
        binding.cbSaturday.isChecked
        binding.cbSunday.isChecked

        when (binding.radiogrpColor.checkedRadioButtonId) {
            binding.radiobtn1.id -> Toast.makeText(this, "1", Toast.LENGTH_LONG).show()
            binding.radiobtn2.id -> Toast.makeText(this, "2", Toast.LENGTH_LONG).show()
            binding.radiobtn3.id -> Toast.makeText(this, "3", Toast.LENGTH_LONG).show()
            binding.radiobtn4.id -> Toast.makeText(this, "4", Toast.LENGTH_LONG).show()
            binding.radiobtn5.id -> Toast.makeText(this, "5", Toast.LENGTH_LONG).show()
        }
    }
}
