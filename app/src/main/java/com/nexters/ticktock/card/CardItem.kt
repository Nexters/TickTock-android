package com.nexters.ticktock.card

import com.nexters.ticktock.dto.DayGroup

data class CardItem(
        var title: String,
        var startTime: String,
        var endTime: String,
        var days: DayGroup
) {
    fun getTime(): String =
            "$startTime~$endTime"
}