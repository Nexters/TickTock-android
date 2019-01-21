package com.nexters.ticktock

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.nexters.ticktock.dao.DatabaseHelper

abstract class OrmAppCompatActivity : AppCompatActivity(){

    protected val databaseHelper: DatabaseHelper by lazy { DatabaseHelper(this) }

    override fun onDestroy() {
        super.onDestroy()
        OpenHelperManager.releaseHelper()
    }
}