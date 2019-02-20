package com.nexters.ticktock.setting

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nexters.ticktock.CardActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        binding.backToMainBtn.setOnClickListener {

            val mainIntent = Intent(this, CardActivity::class.java)
            startActivity(mainIntent)
            overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2)
    }
}
