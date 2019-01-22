package com.nexters.ticktock.timer

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class PrefUtils(c: Context){
    private val STARTED_TIME_ID = "nexters.ticktock"
    private var mPreferences : SharedPreferences? = null

    init {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c)
    }

    fun getStartedTime() : Long {
        return mPreferences!!.getLong(STARTED_TIME_ID, 0)
    }

    fun setStartedTime(started : Long) {
        var editor : SharedPreferences.Editor = mPreferences!!.edit()
        editor.putLong(STARTED_TIME_ID, started)
        editor.apply()
    }
}