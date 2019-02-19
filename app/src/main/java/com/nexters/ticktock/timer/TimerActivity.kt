package com.nexters.ticktock.timer

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.view.ViewCompat.animate
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityTimerBinding
import java.util.*


class TimerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTimerBinding
    private val START_TIME_IN_MILLIS: Long = 600000
    var TIMER_LENGTH : Long = 0

    var mCountDownTimer: CountDownTimer? = null // same

    var curPos = 0

    private var mTimerRunning: Boolean = false
    private enum class TimerState {
        RUNNING, STOPPED
    }
    private var mState : TimerState? = null

    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    var mTimeToGo : Long? = null
    private var mProgressTime : Float? = null

    private lateinit var timerRecyclerViewAdapter: TimerRecyclerViewAdapter
    lateinit var stepList : MutableList<TimerStepItem>
    lateinit var stepTimeList : MutableList<String>

    lateinit var mPreferences : PrefUtils

    var mProgressBarAnimator: ObjectAnimator? = null

    private var snapHelper: ControllableTimerSnapHelper? = null

    var expiredTimeIdx : Int? = 0

    var isTimerAlarmed = false
    var checkIntentAlarm = false;
    var isStarted = false;
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer)

        stepList = mutableListOf(
                TimerStepItem("00:20"),
                TimerStepItem("10:00"),
                TimerStepItem("15:00"),
                TimerStepItem("출발하세요!")
        )

        stepTimeList = mutableListOf(
                "샤워하기",
                "머리말리기",
                "옷입기"
        )
        binding.tvTitle.text = "출근 알람알람알람"
        binding.tvStep.text = stepTimeList[0]
        snapHelper = ControllableTimerSnapHelper(this, binding.tvStep, stepTimeList, binding.buttonNext, binding.buttonReset)

        timerRecyclerViewAdapter = TimerRecyclerViewAdapter(this, stepList, binding.rvTimer, snapHelper!!)

        binding.closeTimerBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        binding.rvTimer.apply {
            layoutManager = SpeedControllableTimerLayoutManager(
                    this@TimerActivity, LinearLayoutManager.HORIZONTAL, false, this, 50F)
            addItemDecoration(OffsetTimerItemDecoration(this@TimerActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = timerRecyclerViewAdapter
            snapHelper!!.attachToRecyclerView(this)
        }

        mProgressBarAnimator?.cancel()
        //animate(binding.CircularProgressBar, null, 0f, 1000)


        mPreferences = PrefUtils(this)


        binding.buttonStartPause.setOnClickListener {
            if(mState == TimerState.STOPPED) {
                mPreferences.setStartedTime(getNow())
                startTimer()
                mState = TimerState.RUNNING
            }
        }

        binding.buttonNext.setOnClickListener {

            if(curPos < stepList.size - 2) {
                Log.d("curPosition", curPos.toString())
                binding.rvTimer.smoothScrollToPosition(curPos + 1)
                binding.tvStep.text = stepTimeList[curPos + 1]
                onTimerReset()
                mCountDownTimer!!.cancel()
                val time: List<String> = stepList[curPos + 1].time.split(":")
                val realTime: Long = (time[1].toLong() + time[0].toLong() * 60)

                if(binding.buttonReset.visibility == View.INVISIBLE)
                    binding.buttonReset.visibility = View.VISIBLE
                mTimeToGo = realTime
                mPreferences.setStartedTime(getNow())
                TIMER_LENGTH = realTime
                curPos++
                startTimer()
            }
            else if(curPos == stepList.size - 2){
                binding.rvTimer.smoothScrollToPosition(curPos + 1)
                onTimerReset()
                mCountDownTimer!!.cancel()
                binding.buttonNext.visibility = View.INVISIBLE
                curPos++
            }
        }

        mPreferences.setStartedTime(0)
        binding.buttonReset.setOnClickListener {
            if(curPos > 0) {
                binding.rvTimer.smoothScrollToPosition(curPos - 1)
                binding.tvStep.text = stepTimeList[curPos - 1]
                onTimerReset()
                mCountDownTimer!!.cancel()
                val time: List<String> = stepList[curPos - 1].time.split(":")
                val realTime: Long = (time[1].toLong() + time[0].toLong() * 60)

                if(binding.buttonNext.visibility == View.INVISIBLE)
                    binding.buttonNext.visibility = View.VISIBLE
                if(curPos == 1)
                    binding.buttonReset.visibility = View.INVISIBLE
                mTimeToGo = realTime
                mPreferences.setStartedTime(getNow())
                TIMER_LENGTH = realTime
                curPos--
                startTimer()
            }

        }

        //updateCountDownText()
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

    override fun onBackPressed() {
        return
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        removeAlarm()
    }

    override fun onPause() {
        super.onPause()
        if(mState == TimerState.RUNNING && expiredTimeIdx == 0) {
            mCountDownTimer!!.cancel()
            setAlarm()
        }
    }

    fun getNow() : Long {
        val rightNow : Calendar = Calendar.getInstance()
        return rightNow.timeInMillis / 1000
    }

    private fun initTimer() {
        val startTime : Long = mPreferences.getStartedTime()
        if(startTime > 0) {
            mTimeToGo = (TIMER_LENGTH - (getNow() - startTime))

            if(mTimeToGo!! <= 0) { // Timer expired
                val curProg = binding.CircularProgressBar.progress
                binding.CircularProgressBar.progress = 1.0f
                if(curProg != 1.0f) startTimer()
            }
            else {
                mState = TimerState.RUNNING
                val curProg = binding.CircularProgressBar.progress
                Log.d("curPos", "isError")
                if(curProg != 1.0f) startTimer()
            }
        }
        else {
            mTimeToGo = TIMER_LENGTH
            mState = TimerState.STOPPED

            //타이머 처음 스탭부터 자동시작
            val time : List<String> = stepList[0].time.split(":")
            val realTime : Long = (time[1].toLong() + time[0].toLong() * 60 )
            TIMER_LENGTH = realTime
            mTimeToGo = realTime
            binding.buttonStartPause.performClick()
        }
        updateCountDownText()
    }

    fun onTimerReset() {
        expiredTimeIdx = 0
        mPreferences.setStartedTime(0)
        mTimeToGo = TIMER_LENGTH
        binding.timerLayout.setBackgroundColor(Color.parseColor("#ffffff"))
        updateCountDownText()
    }

    private fun onTimerFinished() {
        Toast.makeText(this, R.string.timer_finished, Toast.LENGTH_LONG).show()

        updateCountDownText()
    }

    fun startTimer() {
        //Log.d("CurrentPos", curPos.toString())
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                //mTimeLeftInMillis = millisUntilFinished

                //알람 울리고 다시 키면 시간초과 화면 뜨게하기
                isStarted = true
                isTimerAlarmed = intent.getBooleanExtra("isAlarmed", false)

                if(isTimerAlarmed && !checkIntentAlarm) {
                    expiredTimeIdx = expiredTimeIdx!! - 1
                    mTimeToGo = Math.abs(expiredTimeIdx!!.toLong())
                    isTimerAlarmed = false
                    checkIntentAlarm = !checkIntentAlarm

                    binding.timerLayout.apply{
                        if(mTimeToGo!!.toInt() % 2 == 1) {
                            setBackgroundColor(Color.parseColor("#ffa7a7"))
                        }
                        else
                            setBackgroundColor(Color.parseColor("#ffffff"))
                    }
                }
                else if(mTimeToGo!! > 0 && expiredTimeIdx == 0) {
                    mTimeToGo = mTimeToGo!! - 1
                    mProgressBarAnimator?.cancel()
                    mProgressTime = Math.abs(1 - mTimeToGo!! / TIMER_LENGTH.toFloat())
                    animate(binding.CircularProgressBar, null, mProgressTime!!, 500)
                }
                else {
                    expiredTimeIdx = expiredTimeIdx!! - 1
                    mTimeToGo = Math.abs(expiredTimeIdx!!.toLong())

                    //시간 초과되면 신호 알리기
                    binding.timerLayout.apply{
                        if(mTimeToGo!!.toInt() % 2 == 1)
                            setBackgroundColor(Color.parseColor("#ffa7a7"))
                        else
                            setBackgroundColor(Color.parseColor("#ffffff"))
                    }
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

        if((mTimeToGo != null) and (curPos != stepList.size - 1)) {
            Log.d("TimeToGO", mTimeToGo.toString())
            var curTime = mTimeToGo
            val minute = mTimeToGo!! / 60
            val second = mTimeToGo!! -  (minute * 60)

            val timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minute, second)
            stepList[curPos].time = timeLeft
            binding.rvTimer.adapter!!.notifyDataSetChanged()
        }
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
