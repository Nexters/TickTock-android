package com.nexters.ticktock.utils

class Hour(value: Int) : TickTockTime(value * 60)

class Minute(value: Int) : TickTockTime(value)

/** time is minute */
open class TickTockTime protected constructor(time:Int) {
    private val time: Int = (time + WHOLE_DAY) % WHOLE_DAY // 24h -> 0, -4h -> 20h

    val hour = ((time / ONE_HOUR) % 12).let {
        if (it == 0)
            12
        else
            it
    }
    val minute = time % ONE_HOUR
    val meridiem =
            if (time < HALF_DAY) {
                AM
            } else {
                PM
            }

    companion object {
        const val ONE_HOUR = 60

        const val WHOLE_DAY = 60 * 24
        const val HALF_DAY = 60 * 12

        const val PM = "PM"
        const val AM = "AM"
    }

    operator fun plus(other: TickTockTime): TickTockTime =
            TickTockTime(this.time + other.time)

    operator fun minus(other: TickTockTime): TickTockTime =
            TickTockTime(this.time - other.time)

    override fun toString(): String =
            "$hour:$minute $meridiem"
}
