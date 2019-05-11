package com.nexters.ticktock.alarmsetting

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.nexters.ticktock.Constant
import com.nexters.ticktock.Constant.ALARM_SETTING_FIRST_REQUEST_CODE
import com.nexters.ticktock.Constant.GPS_ENABLE_REQUEST_CODE
import com.nexters.ticktock.R
import com.nexters.ticktock.autocomplete.AutoCompleteActivity
import com.nexters.ticktock.databinding.ActivityAlarmSettingFirstBinding
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.utils.*
import kotlinx.android.synthetic.main.activity_card.*
import java.util.*

class AlarmSettingFirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmSettingFirstBinding

    private var dayList: EnumSet<Day> = EnumSet.noneOf(Day::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_setting_first)

        binding.firstSettingHeadText.text = getHighlightedString(resources.getString(R.string.first_setting_head_text))


        binding.firstSettingBackButton.setOnClickListener {
            finish()
        }

        binding.firstSettingNextButton.setOnClickListener {
            AlarmSettingSecondActivity.editMode = 1

            val endTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.firstSettingTimePicker.hour.hour() + binding.firstSettingTimePicker.minute.minute()
            } else {
                binding.firstSettingTimePicker.currentHour.hour() + binding.firstSettingTimePicker.currentMinute.minute()
            }

            val intent: Intent = Intent(this, AlarmSettingSecondActivity::class.java)
            intent.putExtra("endTime", endTime)
            intent.putExtra("daySet", dayList)
            startActivity(intent)
        }

        selectDay()
    }

    override fun onResume() {
        super.onResume()

        activeNextButton()
    }

    private fun activeNextButton() {
        if (dayList.isEmpty()) {
            binding.firstSettingNextButton.isEnabled = false
            binding.firstSettingNextButton.setBackgroundColor(Color.parseColor("#D8D8D8"))
        } else {
            binding.firstSettingNextButton.isEnabled = true
            binding.firstSettingNextButton.setBackgroundColor(Color.parseColor("#f6460f"))
        }
    }

    private fun selectDay() {
        binding.firstSettingMon.setOnClickListener {
            if (dayList.contains(Day.Monday)) {
                binding.firstSettingMon.setImageResource(R.drawable.btn_mon_dis)
                dayList.remove(Day.Monday)
            } else {
                binding.firstSettingMon.setImageResource(R.drawable.btn_mon_sel)
                dayList.add(Day.Monday)
            }

            activeNextButton()
        }

        binding.firstSettingTue.setOnClickListener {
            if (dayList.contains(Day.Tuesday)) {
                binding.firstSettingTue.setImageResource(R.drawable.btn_tue_dis)
                dayList.remove(Day.Tuesday)
            } else {
                binding.firstSettingTue.setImageResource(R.drawable.btn_tue_sel)
                dayList.add(Day.Tuesday)
            }

            activeNextButton()
        }

        binding.firstSettingWed.setOnClickListener {
            if (dayList.contains(Day.Wednesday)) {
                binding.firstSettingWed.setImageResource(R.drawable.btn_wed_dis)
                dayList.remove(Day.Wednesday)
            } else {
                binding.firstSettingWed.setImageResource(R.drawable.btn_wed_sel)
                dayList.add(Day.Wednesday)
            }

            activeNextButton()
        }

        binding.firstSettingThur.setOnClickListener {
            if (dayList.contains(Day.Thursday)) {
                binding.firstSettingThur.setImageResource(R.drawable.btn_thur_dis)
                dayList.remove(Day.Thursday)
            } else {
                binding.firstSettingThur.setImageResource(R.drawable.btn_thur_sel)
                dayList.add(Day.Thursday)
            }

            activeNextButton()
        }

        binding.firstSettingFri.setOnClickListener {
            if (dayList.contains(Day.Friday)) {
                binding.firstSettingFri.setImageResource(R.drawable.btn_fri_dis)
                dayList.remove(Day.Friday)
            } else {
                binding.firstSettingFri.setImageResource(R.drawable.btn_fri_sel)
                dayList.add(Day.Friday)
            }

            activeNextButton()
        }

        binding.firstSettingSat.setOnClickListener {
            if (dayList.contains(Day.Saturday)) {
                binding.firstSettingSat.setImageResource(R.drawable.btn_sat_dis)
                dayList.remove(Day.Saturday)

            } else {
                binding.firstSettingSat.setImageResource(R.drawable.btn_sat_sel)
                dayList.add(Day.Saturday)
            }

            activeNextButton()
        }

        binding.firstSettingSun.setOnClickListener {
            if (dayList.contains(Day.Sunday)) {
                binding.firstSettingSun.setImageResource(R.drawable.btn_sun_dis)
                dayList.remove(Day.Sunday)
            } else {
                binding.firstSettingSun.setImageResource(R.drawable.btn_sun_sel)
                dayList.add(Day.Sunday)
            }

            activeNextButton()
        }
    }
}