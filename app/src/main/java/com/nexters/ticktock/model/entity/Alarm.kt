package com.nexters.ticktock.model.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.nexters.ticktock.dao.converter.DayConverter
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.model.enums.TickTockColor
import com.nexters.ticktock.utils.Time
import java.util.*

@Entity
data class Alarm(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,

        @TypeConverters(DayConverter::class)
        var days: EnumSet<Day>,

        var title: String,

        var startLocation: String,

        var endLocation: String,

        @TypeConverters(TickTockColor::class)
        var color: TickTockColor,

        var enable: Boolean,

        @Embedded var endTime: Time,

        @Embedded var travelTime: Time
)