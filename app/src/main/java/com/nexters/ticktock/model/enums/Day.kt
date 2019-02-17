package com.nexters.ticktock.model.enums

/**
 * enum 은 UPPERCASE 로 써야 하지만
 * 요일 편하게 쓰려고 그냥 썼음
 */
enum class Day(val dayName: String) {

    Monday("월"),
    Tuesday("화"),
    Wednesday("수"),
    Thursday("목"),
    Friday("금"),
    Weekday("주중"),
    Saturday("토"),
    Sunday("일"),
    Weekend("주말")
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