package com.nexters.ticktock.model.entity

import android.arch.persistence.room.*
import com.nexters.ticktock.utils.Time

@Entity(foreignKeys = [
    ForeignKey(
        entity = Alarm::class,
        parentColumns = ["id"],
        childColumns = ["alarmId"]
    )
])
data class Step(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,

        var name: String,

        var order: Int,

        @Embedded var duration: Time,

        var alarmId: Int
)