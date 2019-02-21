package com.nexters.ticktock.utils

fun Int.hour() =
        Time(this * 60)

fun Int.minute() =
        Time(this)

fun Long.hour() =
        Time(this.toInt() * 60)

fun Long.minute() =
        Time(this.toInt())

/** time is minute */
class Time(minute:Int) : Comparable<Time> {

    val time: Int = ((minute + WHOLE_DAY) % WHOLE_DAY) // 24h -> 0, -4h -> 20h

    val hour = ((time / ONE_HOUR) % 12).let {
        if (it == 0)
            12
        else
            it
    }
    val minute = (time + ONE_HOUR) % ONE_HOUR
    val meridiem =
            if (time < HALF_DAY) {
                AM
            } else {
                PM
            }

    companion object {
        const val ONE_HOUR = 60

        const val WHOLE_DAY = ONE_HOUR * 24
        const val HALF_DAY = ONE_HOUR * 12

        const val PM = "PM"
        const val AM = "AM"
    }

    operator fun plus(other: Time): Time =
            Time(this.time + other.time)

    operator fun minus(other: Time): Time =
            Time(this.time - other.time)

    override fun toString(): String =
            "$hour:" + if (minute < 10) "0$minute $meridiem" else "$minute $meridiem"

    override fun compareTo(other: Time): Int =
            this.time - other.time
}
