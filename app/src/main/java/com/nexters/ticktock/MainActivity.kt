package com.nexters.ticktock

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.databinding.DataBindingUtil
import com.nexters.ticktock.autocomplete.AutoCompleteActivity
import com.nexters.ticktock.autocomplete.GPSInfo
import com.nexters.ticktock.databinding.ActivityMainBinding
import com.nexters.ticktock.dto.DayGroup
import com.nexters.ticktock.dto.entity.Article


class MainActivity : OrmAppCompatActivity(), View.OnClickListener {

    val TAG:String = "MainActivity"

    val MAIN_ACTIVITY_REQUEST_CODE = 1111
    val GPS_ENABLE_REQUEST_CODE = 2001

    private lateinit var binding : ActivityMainBinding

    private lateinit var gps: GPSInfo // gps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        gps = GPSInfo(this) // GPS
        gps.isGPSConnected()

        binding.button.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // gps 설정 변경 후 재연결
            GPS_ENABLE_REQUEST_CODE -> gps.getLocation()

            MAIN_ACTIVITY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        binding.button.text =  "${data?.getIntExtra("totalTime", 0)}분이 걸립니다."
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.button.id -> {
                newActivity()
            }
        }
    }

    fun newActivity() {
        val intent = Intent(this, AutoCompleteActivity::class.java)
        intent.putExtra("GPS_RESULT", gps.getResult())
        startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_CODE)
    }
}
