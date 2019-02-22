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
                var startHour = alarm.endTime.time / 60
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
