package com.nexters.ticktock.timer

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.nexters.ticktock.R
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.databinding.ActivityPriorTimerBinding
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.model.Step
import java.text.SimpleDateFormat
import java.util.*



class PriorTimerActivity : AppCompatActivity(){

    var timeHandler : Handler? = null
    var Runnable : Runnable? = null
    var isAm : Boolean? = true // true : am, false : pm
    var curAlarm : Alarm? = null

    private lateinit var binding : ActivityPriorTimerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags((WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED))
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        binding = DataBindingUtil.setContentView(this, com.nexters.ticktock.R.layout.activity_prior_timer)

        /*val mCalendar : Calendar = Calendar.getInstance()
        mCalendar.set(Calendar.HOUR_OF_DAY, 20)
        mCalendar.set(Calendar.MINUTE, 33)
        mCalendar.set(Calendar.SECOND, 0)


        val mAlarmIntent:Intent = Intent(this, AlarmReceiver::class.java)
        val pIntent : PendingIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)*/

        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /*val alarmDao = TickTockDBHelper.getInstance(this).alarmDao
        val alarmList = alarmDao.findAll()

        for(mini in alarmList) {
            val stepSet = mini.steps
            val calendarSet = Calendar.getInstance()
            var startHour = mini.endTime.hour - mini.travelTime.hour
            var startMinute = mini.endTime.minute - mini.travelTime.minute

            for(miniStep in stepSet) {
                startHour -= miniStep.duration.hour
                startMinute -= miniStep.duration.minute     // 하나의 알람에 있는 startHour, startMinute을 구한다.
            }

            calendarSet.set(Calendar.HOUR_OF_DAY, startHour)
            calendarSet.set(Calendar.MINUTE, startMinute)

            val mAlarmIntent:Intent = Intent(this, AlarmReceiver::class.java)
            val pIntent : PendingIntent = PendingIntent.getBroadcast(this, mini.id.toInt(), mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

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
        }*/

        val hourStr = SimpleDateFormat("HH", Locale.KOREA).format(Date())
        val minuteStr = SimpleDateFormat("mm", Locale.KOREA).format(Date())

        binding.btnClose.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


        //Handler 사용하여 현재 시간 setting하기
        Runnable  = Runnable {
            var hourStr = SimpleDateFormat("HH", Locale.KOREA).format(Date())
            if(hourStr.toInt() > 12) {
                hourStr = ((hourStr.toInt()) - 12).toString()
                isAm = false
            }
            if(hourStr.toInt() < 10)
                hourStr = "0$hourStr"
            val minuteStr = SimpleDateFormat("mm", Locale.KOREA).format(Date())
            binding.tvTime.text = "$hourStr:$minuteStr"

            binding.tvAmpm.apply {
                if(isAm!!) text = "AM"
                else text = "PM"
            }
            timeHandler!!.postDelayed(Runnable, 1000)
        }

        timeHandler = Handler(Looper.getMainLooper())
        timeHandler!!.postDelayed(Runnable, 10)

        var min = 24 * 60 + 60
        //준비시작 버튼 누르면 바로 타이머 시작
        binding.btnTimerstart.setOnClickListener{

            val alarmDao = TickTockDBHelper.getInstance(this).alarmDao
            val alarmList = alarmDao.findAll()

            for(alarm in alarmList) {
                var startHour = alarm.endTime.hour - alarm.travelTime.hour
                var startMinute = alarm.endTime.minute - alarm.travelTime.minute

                for(step in alarm.steps) {
                    startHour -= step.duration.hour
                    startMinute -= step.duration.minute // alarm 별 start hour, minute 구하기
                }

                val totalStartlMinute = startHour * 60 + startMinute
                val totalMinute = hourStr.toInt() * 60 + minuteStr.toInt()

                alarmList.sortedBy { Math.abs((totalMinute - it.travelTime.time)) }.firstOrNull()

                if(Math.abs(totalMinute - totalStartlMinute) < min) {
                    curAlarm = alarm
                    min = Math.abs(totalMinute - totalStartlMinute)
                }

            }
            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("curAlarm", curAlarm)
            startActivity(intent)
            overridePendingTransition(com.nexters.ticktock.R.anim.slide_in_up, com.nexters.ticktock.R.anim.slide_out_up)
        }
    }
}
