package com.nexters.ticktock.dao

import android.arch.persistence.room.*
import com.nexters.ticktock.model.entity.Alarm
import io.reactivex.Flowable

@Dao
interface  AlarmDao {
    @Query("SELECT * FROM alarm ORDER BY id")
    fun findAll(): Flowable<Alarm>

    @Query("SELECT * FROM alarm WHERE id = :id")
    fun findById(id: Long): Flowable<Alarm>

    @Query("SELECT MAX(id) FROM alarm")
    fun lastAlarmId(): Flowable<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(alarm: Alarm): Long

    @Delete
    fun delete(alarm: Alarm)
}