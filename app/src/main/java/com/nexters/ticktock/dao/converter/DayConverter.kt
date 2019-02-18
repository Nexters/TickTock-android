package com.nexters.ticktock.dao.converter

import android.arch.persistence.room.TypeConverter
import com.nexters.ticktock.model.enums.Day
import java.util.*

class DayConverter {
    @TypeConverter
    fun fromDatabase(value: String): EnumSet<Day> {
        return EnumSet.copyOf(value.split(",")
                .map { Day.valueOf(it) })
    }

    @TypeConverter
    fun toDatabase(days: EnumSet<Day>): String {
        return days.joinToString(",") { it.name }
    }
}