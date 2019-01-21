package com.nexters.ticktock

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.nexters.ticktock.databinding.ActivityTimerBinding
import java.util.*


class TimerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTimerBinding
    private val START_TIME_IN_MILLIS: Long = 600000

    private var mCountDownTimer: CountDownTimer? = null

    private var mTimerRunning: Boolean = false

    private var mTimeLeftInMillis = START_TIME_IN_MILLIS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer)


        binding.buttonStartPause.setOnClickListener {
            if (mTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.buttonReset.setOnClickListener {
            resetTimer()
        }
        updateCountDownText()
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                binding.buttonStartPause.text = "Start"
                binding.buttonStartPause.visibility = View.INVISIBLE
                binding.buttonReset.visibility = View.VISIBLE
            }
        }.start()

        mTimerRunning = true
        binding.buttonStartPause.text = "Pause"
        binding.buttonReset.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        mCountDownTimer!!.cancel()
        mTimerRunning = false
        binding.buttonStartPause.text = "Start"
        binding.buttonReset.visibility = View.VISIBLE
    }

    private fun resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS
        updateCountDownText()
        binding.buttonReset.visibility = View.INVISIBLE
        binding.buttonStartPause.visibility = View.VISIBLE
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        binding.textViewCountdown.text = timeLeftFormatted
    }
}
