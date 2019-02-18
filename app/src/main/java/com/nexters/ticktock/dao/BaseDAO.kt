package com.nexters.ticktock.dao

import android.arch.persistence.room.*

@Dao
interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(type: T): Long

    @Update
    fun update(type: T)

    @Delete
    fun delete(type: T)
}