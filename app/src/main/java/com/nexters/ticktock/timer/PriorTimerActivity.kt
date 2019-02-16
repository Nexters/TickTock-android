package com.nexters.ticktock.timer

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityPriorTimerBinding

class PriorTimerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPriorTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prior_timer)
    }
}
