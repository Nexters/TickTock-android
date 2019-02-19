package com.nexters.ticktock.dao.converter

import com.nexters.ticktock.model.enums.TickTockColor

class TickTockColorConverter {

    fun fromDatabase(value: String): TickTockColor {
        return TickTockColor.valueOf(value)
    }

    fun toDatabase(color: TickTockColor): String {
        return color.name
    }
}