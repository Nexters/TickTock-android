package com.nexters.ticktock.card

import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.utils.Time
import java.util.*

data class CardItem(
        var startTime: Time,
        var endTime: Time,
        var days: EnumSet<Day>,
        var title: String,
        var startLocation: String,
        var endLocation: String,
        var color: String,
        var enable: Boolean
)