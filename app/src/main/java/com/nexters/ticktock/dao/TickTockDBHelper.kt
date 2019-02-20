package com.nexters.ticktock.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.nexters.ticktock.dao.DBInfo.CREATE_ALARM_TABLE_SCRIPT
import com.nexters.ticktock.dao.DBInfo.CREATE_STEP_TABLE_SCRIPT
import com.nexters.ticktock.dao.DBInfo.DB_FILE_NAME
import com.nexters.ticktock.dao.DBInfo.DB_VERSION
import com.nexters.ticktock.dao.DBInfo.DROP_TABLE_SCRIPT

class TickTockDBHelper(context: Context) : SQLiteOpenHelper(context, DB_FILE_NAME, null, DB_VERSION) {

    val alarmDao: AlarmDao = AlarmDao(context)

    companion object {

        @Volatile private var INSTANCE: TickTockDBHelper? = null

        fun getInstance(context: Context): TickTockDBHelper =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: TickTockDBHelper(context).also { INSTANCE = it }
                }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("database", "onCreate db: $db")
        db?.execSQL(CREATE_STEP_TABLE_SCRIPT)
        db?.execSQL(CREATE_ALARM_TABLE_SCRIPT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("database", "onUpgrade")
        onCreate(db)
    }
}

object DBInfo {
    const val DB_VERSION = 1
    const val DB_FILE_NAME = "nexters.ticktock.db"

    var DROP_TABLE_SCRIPT = """
        DROP TABLE IF EXISTS `step`;
        DROP TABLE IF EXISTS `steps`;
        DROP TABLE IF EXISTS `alarm`;
        DROP TABLE IF EXISTS `alarms`;
    """.trimIndent()

    var CREATE_STEP_TABLE_SCRIPT = """
         CREATE TABLE IF NOT EXISTS step (
             `id`	INTEGER PRIMARY KEY AUTOINCREMENT,
             `name`	TEXT,
             `order`	INTEGER,
             `duration`	INTEGER,
             `alarm_id`	INTEGER,
             CONSTRAINT fk_alarm
                 FOREIGN KEY (alarm_id)
                 REFERENCES alarm(id)
                 ON DELETE CASCADE
         )
     """.trimIndent()

    var CREATE_ALARM_TABLE_SCRIPT = """
         CREATE TABLE IF NOT EXISTS alarm (
             `id`	INTEGER PRIMARY KEY AUTOINCREMENT,
             `days`	TEXT,
             `title`	TEXT,
             `start_location`	TEXT,
             `end_location`	TEXT,
             `color`	TEXT,
             `enable`	INTEGER,
             `end_time`	INTEGER,
             `travel_time`	INTEGER
         )
    """.trimIndent()
}