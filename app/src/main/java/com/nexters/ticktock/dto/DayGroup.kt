package com.nexters.ticktock.dto

import android.annotation.SuppressLint
import android.support.v4.util.Preconditions

class DayGroup {

    enum class Day(val dayName: String) {

        MONDAY("월"),
        Tuesday("화"),
        Wednesday("수"),
        Thursday("목"),
        Friday("금"),
        Saturday("토"),
        Sunday("일")
        ;

        companion object {
            private val map = Day.values().associate { it.dayName to it }

            fun valueOfDayName(dayName: String): Day =
                    map[dayName] ?: throw IllegalStateException("\"$dayName\" 에 해당하는 타입이 없습니다.")
        }

        override fun toString(): String {
            return dayName
        }
    }

    val days: List<Day>

    // Preconditions 에 이게 왜 붙음?
    @SuppressLint("RestrictedApi")
    constructor(days: String){

        this.days = days.split(",")
                .map { it.trim() }
                .onEach { Preconditions.checkStringNotEmpty(it) }
                .map { Day.valueOfDayName(it) }
                .toList()
    }

    @SuppressLint("RestrictedApi")
    constructor(days: MutableList<String>){

        this.days = days
                .map { it.trim() }
                .onEach { Preconditions.checkStringNotEmpty(it) }
                .map { Day.valueOfDayName(it) }
                .toList()
    }

    companion object {

        @SuppressLint("RestrictedApi")
        fun of(days: MutableList<Day>): DayGroup =
            DayGroup(days.apply { Preconditions.checkArgument(days.size != 0) }.joinToString(",") { it.dayName })
    }

    override fun toString(): String {
        return this.days.joinToString(",") { it.dayName }
    }
}