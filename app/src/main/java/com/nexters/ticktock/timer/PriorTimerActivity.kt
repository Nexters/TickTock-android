package com.nexters.ticktock.timer

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityPriorTimerBinding
import java.text.SimpleDateFormat
import java.util.*

class PriorTimerActivity : AppCompatActivity() {

    var timeHandler : Handler? = null
    var Runnable : Runnable? = null
    var isAm : Boolean? = true // true : am, false : pm

    private lateinit var binding : ActivityPriorTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prior_timer)


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
    }

}
