package com.nexters.ticktock.card

import com.nexters.ticktock.dto.DayGroup

data class CardItem(
        var destination: String,
        var endTime: String,
        var duration: String,
        var days: DayGroup,
        var memo: String
) {
    fun getTime(): String =
            "10:00~$endTime"
}