package com.nexters.ticktock.model

import com.nexters.ticktock.utils.Time

data class Step(

        var id: Long = 0,

        var name: String,

        var order: Int,

        var duration: Time,

        var alarmId: Long
)