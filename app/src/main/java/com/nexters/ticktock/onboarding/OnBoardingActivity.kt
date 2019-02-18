package com.nexters.ticktock.onboarding

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import com.nexters.ticktock.CardActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityOnboardingBinding
import android.text.method.Touch.onTouchEvent
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.annotation.SuppressLint
import android.view.View


class OnBoardingActivity : AppCompatActivity(), OnBoardingInterface {

    private lateinit var binding : ActivityOnboardingBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.nexters.ticktock.R.layout.activity_onboarding)

        val adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                when(position) {
                    0 -> return OnBoardingScreen1()
                    1 -> return OnBoardingScreen2()
                    2 -> return OnBoardingScreen3()
                    else -> return null
                }
            }

            override fun getCount(): Int {
                return 3
            }
        }

        binding.onboardingVp.adapter = adapter
    }

    override fun finishOnBoarding() {
        val preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        preferences.edit().putBoolean("onboarding_complete", true).apply()

        val main = Intent(this, CardActivity::class.java)
        startActivity(main)

        finish()
    }
}
