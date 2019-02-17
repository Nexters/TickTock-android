package com.nexters.ticktock.utils

import com.nexters.ticktock.model.enums.Day
import java.util.*

fun EnumSet<Day>.getArrangedDays(): EnumSet<Day> {

    val arrangedDays = EnumSet.copyOf(this)

    if (arrangedDays.containsAll(DayGroup.weekday)) {
        arrangedDays.removeAll(DayGroup.weekday)
        arrangedDays.add(Day.Weekday)
    }

    if (arrangedDays.containsAll(DayGroup.weekend)) {
        arrangedDays.removeAll(DayGroup.weekend)
        arrangedDays.add(Day.Weekend)
    }

    return arrangedDays
}

object DayGroup {

    val weekend: EnumSet<Day> = EnumSet.of(Day.Saturday, Day.Sunday)
    val weekday: EnumSet<Day> = EnumSet.of(Day.Monday, Day.Tuesday, Day.Wednesday, Day.Thursday, Day.Friday)
}