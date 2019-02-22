package com.nexters.ticktock.model

import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.model.enums.TickTockColor
import com.nexters.ticktock.utils.Time
import java.io.Serializable
import java.util.*

data class Alarm(
        var id: Long = 0,

        var days: EnumSet<Day>,

        var title: String,

        var startLocation: String,

        var endLocation: String,

        var color: TickTockColor,

        var enable: Boolean,

        var endTime: Time,

        var travelTime: Time,

        var steps: MutableSet<Step> = mutableSetOf()
) : Serializable