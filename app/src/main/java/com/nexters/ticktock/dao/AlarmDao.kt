package com.nexters.ticktock.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.nexters.ticktock.model.entity.Alarm

@Dao
interface  AlarmDao : BaseDao<Alarm> {
    @Query("SELECT * FROM alarm")
    fun findAll(): Array<Alarm>
}