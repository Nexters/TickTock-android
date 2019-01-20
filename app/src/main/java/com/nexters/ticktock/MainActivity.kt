package com.nexters.ticktock

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val TAG:String = "MainActivity"

    val MAIN_ACTIVITY_REQUEST_CODE = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MAIN_ACTIVITY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.d(TAG, "${data?.getIntExtra("totalTime", 0)}")
                        button.text =  "${data?.getIntExtra("totalTime", 0)}분이 걸립니다."
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                newActivity()
            }
        }
    }

    fun newActivity() {
        val intent = Intent(this, AutoCompleteActivity::class.java)
        startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_CODE)
    }
}
