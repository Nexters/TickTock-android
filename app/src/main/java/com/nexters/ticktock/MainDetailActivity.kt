package com.nexters.ticktock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TimePicker
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.databinding.ActivityMainDetailBinding
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.model.enums.TickTockColor
import com.nexters.ticktock.timer.AlarmReceiver
import com.nexters.ticktock.utils.*
import java.util.*

class MainDetailActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainDetailBinding

    private var alarmId: Long = 0
    private var startHour = 0
    private var startMinute = 0
    private var dayList: EnumSet<Day> = EnumSet.noneOf(Day::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_detail)

        getData(intent.getLongExtra("CARD_ID", 0))

        binding.btnClose.setOnClickListener(this)
        binding.btnEditPrepareTime.setOnClickListener(this)
        binding.layoutSave.setOnClickListener(this)

        binding.tpMeetingTime.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener{
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                startHour = hourOfDay
                startMinute = minute
            }
        })

        binding.edMemo.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.tvMemoMax.visibility = View.GONE
                    binding.layoutSave.isClickable = true
                    binding.layoutSave.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.btnEnable))
                }
                else if (count == 0) {
                    binding.tvMemoMax.visibility = View.VISIBLE
                    binding.layoutSave.isClickable = false
                    binding.layoutSave.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.btnDisEnable))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnClose.id -> finish()

            binding.btnEditPrepareTime.id -> {
                val intent = Intent(this, NoneActivity::class.java)
                startActivity(intent)
            }

            binding.layoutSave.id -> {
                setData()

                val alarmDao = TickTockDBHelper.getInstance(this).alarmDao
                val alarmList = alarmDao.findAll()

                // 알람에서 해당 알람 삭제
                for(alarm in alarmList) {
                    if(alarm.id == this.alarmId) {
                        alarmDao.delete(alarm)
                        break
                    }
                }

                //수정된 알람 재 추가
                val calendarSet = Calendar.getInstance()
                val startHour = startHour
                val startMinute = startMinute

                calendarSet.set(Calendar.HOUR_OF_DAY, startHour)
                calendarSet.set(Calendar.MINUTE, startMinute)

                Log.d("StartAlarm", "$startHour:$startMinute")

                val mAlarmIntent:Intent = Intent(this, AlarmReceiver::class.java)
                val pIntent : PendingIntent = PendingIntent.getBroadcast(this, alarmDao.findAll().size, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val rightNow : Calendar = Calendar.getInstance()

                if(rightNow.timeInMillis < calendarSet.timeInMillis) {

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)
                    if(Build.VERSION.SDK_INT >= 23)
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)

                    else if(Build.VERSION.SDK_INT >= 21)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)

                    else
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)
                }

                finish()
            }
        }
    }

    fun getData(id: Long) {
        alarmId = id
        val alarmDao = TickTockDBHelper.getInstance(this).alarmDao
        alarmDao.findAll().forEach { Log.d("AlarmDao", it.toString()) }
        val alarm = alarmDao.findById(id) as Alarm
        val prepareTime = alarm.steps.map { it.duration }.fold(Time(0)) { acc, time -> acc + time }

        binding.tvAlarmTime.text = getResizedString("*${alarm.endTime.hour}:${alarm.endTime.minute}* ${alarm.endTime.meridiem}", 3.125f)
        binding.tvMemo.setText(alarm.title)

        startHour = alarm.endTime.time / 60
        startMinute = alarm.endTime.minute
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.tpMeetingTime.hour = startHour
            binding.tpMeetingTime.minute = startMinute
        } else {
            binding.tpMeetingTime.setCurrentHour(startHour)
            binding.tpMeetingTime.setCurrentMinute(startMinute)
        }

        alarm.days.forEach {
            val checkbox = when (it) {
                Day.Monday -> binding.cbMonday
                Day.Tuesday -> binding.cbTuesday
                Day.Wednesday -> binding.cbWednesday
                Day.Thursday -> binding.cbThursday
                Day.Friday -> binding.cbFriday
                Day.Saturday -> binding.cbSaturday
                Day.Sunday -> binding.cbSunday
                else -> binding.cbMonday
            }
            checkbox.isChecked = true
        }

        when (alarm.color) {
            TickTockColor.RED -> {
                binding.layoutTitle.setBackgroundColor(ContextCompat.getColor(this, TickTockColor.RED.cardBgColorId))
                binding.radiogrpColor.check(binding.radiobtn1.id)
            }
            TickTockColor.GREEN -> {
                binding.layoutTitle.setBackgroundColor(ContextCompat.getColor(this, TickTockColor.GREEN.cardBgColorId))
                binding.radiogrpColor.check(binding.radiobtn2.id)
            }
            TickTockColor.YELLOW -> {
                binding.layoutTitle.setBackgroundColor(ContextCompat.getColor(this, TickTockColor.YELLOW.cardBgColorId))
                binding.radiogrpColor.check(binding.radiobtn3.id)
            }
            TickTockColor.BLUE -> {
                binding.layoutTitle.setBackgroundColor(ContextCompat.getColor(this, TickTockColor.BLUE.cardBgColorId))
                binding.radiogrpColor.check(binding.radiobtn4.id)
            }
            TickTockColor.PURPLE -> {
                binding.layoutTitle.setBackgroundColor(ContextCompat.getColor(this, TickTockColor.PURPLE.cardBgColorId))
                binding.radiogrpColor.check(binding.radiobtn5.id)
            }
        }

        if (prepareTime.time / 60 != 0) binding.tvPrepareTimeSecond.text = "${prepareTime.hour}시간 ${prepareTime.minute}분"
        else binding.tvPrepareTimeSecond.text = "${prepareTime.minute}분"

        binding.edMemo.setText(alarm.title)
    }

    fun setData() {

        if (binding.cbMonday.isChecked) dayList.add(Day.Monday)
        if (binding.cbTuesday.isChecked) dayList.add(Day.Tuesday)
        if (binding.cbWednesday.isChecked) dayList.add(Day.Wednesday)
        if (binding.cbThursday.isChecked) dayList.add(Day.Thursday)
        if (binding.cbFriday.isChecked) dayList.add(Day.Friday)
        if (binding.cbSaturday.isChecked) dayList.add(Day.Saturday)
        if (binding.cbSunday.isChecked) dayList.add(Day.Sunday)

        var color = TickTockColor.RED
        when (binding.radiogrpColor.checkedRadioButtonId) {
            binding.radiobtn1.id -> color = TickTockColor.RED
            binding.radiobtn2.id -> color = TickTockColor.GREEN
            binding.radiobtn3.id -> color = TickTockColor.YELLOW
            binding.radiobtn4.id -> color = TickTockColor.BLUE
            binding.radiobtn5.id -> color = TickTockColor.PURPLE
        }

        val alarmDao = TickTockDBHelper.getInstance(this).alarmDao

        alarmDao.save(Alarm(
                id = alarmId,
                days = dayList,
                title = binding.edMemo.text.toString(),
                startLocation = "",
                endLocation = "",
                color = color,
                enable = true,
                endTime = Time(startHour * 60 + startMinute),
                travelTime = Time(0)
        ))
    }
}
