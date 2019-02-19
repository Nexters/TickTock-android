package com.nexters.ticktock.dao.converter

import com.nexters.ticktock.model.enums.Day
import java.util.*

class DayConverter {
    fun fromDatabase(value: String): EnumSet<Day> {
        return EnumSet.copyOf(value.split(",")
                .map { Day.valueOf(it) })
    }

    fun toDatabase(days: EnumSet<Day>): String {
        return days.joinToString(",") { it.name }
    }
}