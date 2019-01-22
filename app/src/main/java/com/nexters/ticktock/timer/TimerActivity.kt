package com.nexters.ticktock.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityTimerBinding
import java.util.*


class TimerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTimerBinding
    private val START_TIME_IN_MILLIS: Long = 600000
    private val TIMER_LENGTH : Long = 10

    private var mCountDownTimer: CountDownTimer? = null // same

    private var mTimerRunning: Boolean = false
    private enum class TimerState {
        RUNNING, STOPPED
    }
    private var mState : TimerState? = null

    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    private var mTimeToGo : Long? = null

    private lateinit var mPreferences : PrefUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer)

        mPreferences = PrefUtils(this)
        binding.buttonReset.isEnabled = false
        binding.buttonStartPause.setOnClickListener {
            if(mState == TimerState.STOPPED) {
                mPreferences.setStartedTime(getNow())
                startTimer()
                mState = TimerState.RUNNING
                binding.buttonReset.isEnabled = true
            }
        }

        binding.buttonReset.setOnClickListener {
            if(mState == TimerState.RUNNING) {
                mCountDownTimer!!.cancel()
                mState = TimerState.STOPPED
                onTimerReset()
            }
        }

        updateCountDownText()
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        removeAlarm()
    }

    override fun onPause() {
        super.onPause()
        if(mState == TimerState.RUNNING) {
            mCountDownTimer!!.cancel()
            setAlarm()
        }
    }

    private fun getNow() : Long {
        val rightNow : Calendar = Calendar.getInstance()
        return rightNow.timeInMillis / 1000
    }

    private fun initTimer() {
        val startTime : Long = mPreferences.getStartedTime()
        if(startTime > 0) {
            mTimeToGo = (TIMER_LENGTH - (getNow() - startTime))
            if(mTimeToGo!! <= 0) { // Timer expired
                mTimeToGo = TIMER_LENGTH
                mState = TimerState.STOPPED
                onTimerFinished()
            }
            else {
                startTimer()
                mState = TimerState.RUNNING
            }
        }
        else {
            mTimeToGo = TIMER_LENGTH
            mState = TimerState.STOPPED
        }
        updateCountDownText()
    }

    private fun onTimerReset() {
        mPreferences.setStartedTime(0)
        mTimeToGo = TIMER_LENGTH
        binding.buttonReset.isEnabled = false
        updateCountDownText()
    }

    private fun onTimerFinished() {
        Toast.makeText(this, R.string.timer_finished, Toast.LENGTH_LONG).show()
        mPreferences.setStartedTime(0)
        mTimeToGo = TIMER_LENGTH
        updateCountDownText()
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //mTimeLeftInMillis = millisUntilFinished
                if(mTimeToGo!! > 0) mTimeToGo = mTimeToGo!! - 1
                else {
                    mCountDownTimer!!.cancel()
                    mPreferences.setStartedTime(0)
                    mState = TimerState.STOPPED
                    mTimeToGo = TIMER_LENGTH
                }

                updateCountDownText()
            }

            override fun onFinish() {
                mState = TimerState.STOPPED
                onTimerFinished()
                updateCountDownText()
            }
        }.start()
    }



    private fun updateCountDownText() {
        binding.buttonStartPause.isEnabled = mState != TimerState.RUNNING
        val timeLeft = String.format(Locale.getDefault(), "00:%02d", mTimeToGo)
        binding.textViewCountdown.text = timeLeft
    }

    private fun setAlarm() {
        val wakeupTime : Long = (mPreferences.getStartedTime() + TIMER_LENGTH)* 1000
        val am : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent : Intent = Intent(this, TimerExpiredReceiver::class.java)
        val sender : PendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            am.setAlarmClock(AlarmManager.AlarmClockInfo(wakeupTime, sender), sender)
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, wakeupTime, sender)
        }
    }

    private fun removeAlarm() {
        val intent : Intent = Intent(this, TimerExpiredReceiver::class.java)
        val sender : PendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val am : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(sender)
    }

}
