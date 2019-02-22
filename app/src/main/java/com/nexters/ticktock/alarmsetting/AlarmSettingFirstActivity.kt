package com.nexters.ticktock.alarmsetting

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
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
import com.nexters.ticktock.autocomplete.GPSInfo
import com.nexters.ticktock.databinding.ActivityAlarmSettingFirstBinding
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.utils.Time
import com.nexters.ticktock.utils.getUnderlinedString
import com.nexters.ticktock.utils.hour
import com.nexters.ticktock.utils.minute
import java.util.*

class AlarmSettingFirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmSettingFirstBinding

    private lateinit var gps: GPSInfo // gps
    private var dayList: EnumSet<Day> = EnumSet.noneOf(Day::class.java)

    private var hour: Int? = null
    private var minute: Int? = null
    private var startLocation: String? = null
    private var endLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_setting_first)

        gps = GPSInfo(this) // GPS
        gps.isGPSConnected()

        binding.firstSettingDirectionButton.setOnClickListener {
            val intent = Intent(this, AutoCompleteActivity::class.java)
            intent.putExtra("GPS_RESULT", gps.getResult())
            startActivityForResult(intent, Constant.ALARM_SETTING_FIRST_REQUEST_CODE)
        }

        binding.firstSettingBackButton.setOnClickListener {
            finish()
        }

        binding.firstSettingNextButton.setOnClickListener {
            AlarmSettingSecondActivity.editMode = 1


            val travelTime = (hour?.hour() ?: Time(0)) + (minute?.minute() ?: Time(0))

            val endTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.firstSettingTimePicker.hour.hour() + binding.firstSettingTimePicker.minute.minute()
            } else {
                binding.firstSettingTimePicker.currentHour.hour() + binding.firstSettingTimePicker.currentMinute.minute()
            }

            val intent: Intent = Intent(this, AlarmSettingSecondActivity::class.java)
            intent.putExtra("endTime", endTime)
            intent.putExtra("daySet", dayList)
            intent.putExtra("startLocation", startLocation)
            intent.putExtra("endLocation", endLocation)
            intent.putExtra("travelTime", travelTime)
            startActivity(intent)
        }

        selectDay()
    }

    override fun onResume() {
        super.onResume()

        if (endLocation == null) {
            binding.firstSettingDirectionBefore.visibility = View.VISIBLE
            binding.firstSettingDirectionAfter.visibility = View.GONE
        } else {
            binding.firstSettingDirectionBefore.visibility = View.GONE
            binding.firstSettingDirectionAfter.visibility = View.VISIBLE

            if (hour != null) {
                binding.firstSettingTotalTimeText.text = "총 ".plus(hour.toString()).plus("시간 ").plus(minute.toString()).plus("분 소요")
            } else {
                binding.firstSettingTotalTimeText.text = "총 ".plus(minute).plus("분 소요")
            }
            /* TODO: 언더라인 두개로 만들어야함*/
            binding.firstSettingLocationText.text = getUnderlinedString("*$startLocation*에서 *$endLocation* 까지")
        }

        if (endLocation == null || dayList.isEmpty()) {
            /* TODO: 버튼 비활성화 */
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
        }

        binding.firstSettingTue.setOnClickListener {
            if (dayList.contains(Day.Tuesday)) {
                binding.firstSettingTue.setImageResource(R.drawable.btn_tue_dis)
                dayList.remove(Day.Tuesday)
            } else {
                binding.firstSettingTue.setImageResource(R.drawable.btn_tue_sel)
                dayList.add(Day.Tuesday)
            }
        }

        binding.firstSettingWed.setOnClickListener {
            if (dayList.contains(Day.Wednesday)) {
                binding.firstSettingWed.setImageResource(R.drawable.btn_wed_dis)
                dayList.remove(Day.Wednesday)
            } else {
                binding.firstSettingWed.setImageResource(R.drawable.btn_wed_sel)
                dayList.add(Day.Wednesday)
            }
        }

        binding.firstSettingThur.setOnClickListener {
            if (dayList.contains(Day.Thursday)) {
                binding.firstSettingThur.setImageResource(R.drawable.btn_thur_dis)
                dayList.remove(Day.Thursday)
            } else {
                binding.firstSettingThur.setImageResource(R.drawable.btn_thur_sel)
                dayList.add(Day.Thursday)
            }
        }

        binding.firstSettingFri.setOnClickListener {
            if (dayList.contains(Day.Friday)) {
                binding.firstSettingFri.setImageResource(R.drawable.btn_fri_dis)
                dayList.remove(Day.Friday)
            } else {
                binding.firstSettingFri.setImageResource(R.drawable.btn_fri_sel)
                dayList.add(Day.Friday)
            }
        }

        binding.firstSettingSat.setOnClickListener {
            if (dayList.contains(Day.Saturday)) {
                binding.firstSettingSat.setImageResource(R.drawable.btn_sat_dis)
                dayList.remove(Day.Saturday)

            } else {
                binding.firstSettingSat.setImageResource(R.drawable.btn_sat_sel)
                dayList.add(Day.Saturday)
            }
        }

        binding.firstSettingSun.setOnClickListener {
            if (dayList.contains(Day.Sunday)) {
                binding.firstSettingSun.setImageResource(R.drawable.btn_sun_dis)
                dayList.remove(Day.Sunday)
            } else {
                binding.firstSettingSun.setImageResource(R.drawable.btn_sun_sel)
                dayList.add(Day.Sunday)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // gps 설정 변경 후 재연결
            GPS_ENABLE_REQUEST_CODE -> gps.getLocation()

            ALARM_SETTING_FIRST_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (data?.getIntExtra("DIRECTION_TIME_HOUR", 0) != 0) {
                            hour = data?.getIntExtra("DIRECTION_TIME_HOUR", 0)
                            minute = data?.getIntExtra("DIRECTION_TIME_MINUTE", 0)
                        } else {
                            hour = null
                            minute = data.getIntExtra("DIRECTION_TIME_MINUTE", 0)
                        }
                        startLocation = data?.getStringExtra("DIRECTION_START")
                        endLocation = data?.getStringExtra("DIRECTION_DESTINATION")
                    }
                }
            }
        }
    }
}