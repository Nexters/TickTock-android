package com.nexters.ticktock.timer

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityTimerBinding
import java.util.*


class TimerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTimerBinding
    private val START_TIME_IN_MILLIS: Long = 600000
    private val TIMER_LENGTH : Long = 10

    var mCountDownTimer: CountDownTimer? = null // same



    private var mTimerRunning: Boolean = false
    private enum class TimerState {
        RUNNING, STOPPED
    }
    private var mState : TimerState? = null

    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    private var mTimeToGo : Long? = null
    private var mProgressTime : Float? = null

    private lateinit var timerRecyclerViewAdapter: TimerRecyclerViewAdapter
    private lateinit var stepList : MutableList<TimerStepItem>

    private lateinit var mPreferences : PrefUtils

    var mProgressBarAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer)

        stepList = mutableListOf(
                TimerStepItem("샤워하기", "00:30:00"),
                TimerStepItem("머리말리기", "00:10:00"),
                TimerStepItem("옷입기", "00:15:00")
        )
        val snapHelper = ControllableTimerSnapHelper(this, binding.CircularProgressBar)

        timerRecyclerViewAdapter = TimerRecyclerViewAdapter(this, stepList, binding.rvTimer, snapHelper)

        binding.rvTimer.apply {
            layoutManager = SpeedControllableTimerLayoutManager(
                    this@TimerActivity, LinearLayoutManager.HORIZONTAL, false, this, 50F)
            addItemDecoration(OffsetTimerItemDecoration(this@TimerActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = timerRecyclerViewAdapter
            snapHelper.attachToRecyclerView(this)
        }

        mProgressBarAnimator?.cancel()
        //animate(binding.CircularProgressBar, null, 0f, 1000)


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
                animate(binding.CircularProgressBar, null, 0.0f, 500)
                onTimerReset()
            }
            else
                animate(binding.CircularProgressBar, null, 0.0f, 500)
        }

        updateCountDownText()
    }

    private fun animate(progressBar: CircularProgressbar,
                        listener: AnimatorListener) {
        val progress = (Math.random() * 2).toFloat()
        val duration = 3000
        animate(progressBar, listener, progress, duration)
    }

    fun animate(progressBar: CircularProgressbar, listener: AnimatorListener?, progress: Float, duration: Int) {

        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress)
        mProgressBarAnimator!!.duration = duration.toLong()

        mProgressBarAnimator!!.addListener(object : AnimatorListener {

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationStart(animation: Animator) {}
        })
        if (listener != null) {
            mProgressBarAnimator!!.addListener(listener)
        }
        mProgressBarAnimator!!.reverse()
        mProgressBarAnimator!!.addUpdateListener(AnimatorUpdateListener { animation -> progressBar.progress = animation.animatedValue as Float })
        progressBar.markerProgress = progress
        mProgressBarAnimator!!.start()
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

    fun onTimerReset() {
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

    fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //mTimeLeftInMillis = millisUntilFinished
                if(mTimeToGo!! > 0) {
                    mTimeToGo = mTimeToGo!! - 1
                    val percentage = (100 / TIMER_LENGTH).toFloat()
                    mProgressBarAnimator?.cancel()
                    mProgressTime = ((TIMER_LENGTH - mTimeToGo!!) / percentage)
                    animate(binding.CircularProgressBar, null, mProgressTime!!, 500)

                    Log.d("ProgressTime", mProgressTime.toString())
                }
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

        val timeLeft = String.format(Locale.getDefault(), "00:00:%02d", mTimeToGo)
        //Log.d("ProgressTime", mProgressTime.toString())
        //binding.textViewCountdown.text = timeLeft
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
        val intent = Intent(this, TimerExpiredReceiver::class.java)
        val sender : PendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val am : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(sender)
    }

}
