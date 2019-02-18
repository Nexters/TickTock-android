package com.nexters.ticktock.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.nexters.ticktock.model.entity.Step

@Dao
interface StepDao : BaseDao<Step> {

    @Query("SELECT * FROM step")
    fun findAll(): Array<Step>
}