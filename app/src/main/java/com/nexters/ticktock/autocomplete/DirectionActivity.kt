package com.nexters.ticktock.autocomplete

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityDirectionBinding

// TODO 임시적용
class DirectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDirectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direction)

        val directionPagerAdapter = DirectionPagerAdapter(supportFragmentManager)
        binding.viewpagerDirection.adapter = directionPagerAdapter
    }
}
