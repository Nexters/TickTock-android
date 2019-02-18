//package com.nexters.ticktock.dao
//
//import android.arch.persistence.room.Database
//import android.arch.persistence.room.Room
//import android.arch.persistence.room.RoomDatabase
//import android.arch.persistence.room.TypeConverters
//import android.content.Context
//import com.nexters.ticktock.dao.converter.DayConverter
//import com.nexters.ticktock.dao.converter.TickTockColorConverter
//import com.nexters.ticktock.model.entity.Alarm
//import com.nexters.ticktock.model.entity.Step
//import com.nexters.ticktock.utils.SingletonHolder
//
//@Database(entities = [Alarm::class, Step::class], version = 1)
//@TypeConverters(DayConverter::class, TickTockColorConverter::class)
//abstract class TickTockDatabase : RoomDatabase() {
//    abstract fun alarmDao(): AlarmDao
//    abstract fun stepDao(): StepDao
//
//    companion object : SingletonHolder<TickTockDatabase, Context> ({
//        Room.databaseBuilder(it.applicationContext,
//                TickTockDatabase::class.java, "ticktock.db")
//                .build()
//    })
//}