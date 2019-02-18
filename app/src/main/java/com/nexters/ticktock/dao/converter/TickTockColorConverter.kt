package com.nexters.ticktock.dao.converter

import android.arch.persistence.room.TypeConverter
import com.nexters.ticktock.model.enums.TickTockColor

class TickTockColorConverter {

    @TypeConverter
    fun fromDatabase(value: String): TickTockColor {
        return TickTockColor.valueOf(value)
    }

    @TypeConverter
    fun toDatabase(color: TickTockColor): String {
        return color.name
    }
}