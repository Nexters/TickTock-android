package com.nexters.ticktock.utils

import android.support.v7.app.AppCompatActivity
import com.nexters.ticktock.autocomplete.GPSInfo

class Location{

    companion object {

        private var INSTANCE: GPSInfo? = null

        fun getInstance(ac: AppCompatActivity): GPSInfo =
                INSTANCE ?: GPSInfo(ac).also { INSTANCE = it }
    }
}