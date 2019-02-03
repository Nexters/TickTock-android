package com.nexters.ticktock.dto.entity

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import com.nexters.ticktock.dto.DayGroup
import com.nexters.ticktock.dto.Vehicle
import java.util.*

@DatabaseTable(tableName = "articles")
class Article(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "end_time", canBeNull = false)
        var endTime: Date,

        @DatabaseField(columnName = "extra_time", canBeNull = false)
        var extraTime: Int,

        @DatabaseField(columnName = "color", canBeNull = true)
        var color: String,

        @DatabaseField(columnName = "isActive", canBeNull = false)
        var isActive: Boolean,

        destination: Destination,

        days: DayGroup
){

    constructor(id: Int = 0,
                endTime: Date,
                extraTime: Int,
                color: String,
                isActive: Boolean,
                destinationName: String,
                travelTime: Int,
                days: DayGroup,
                vehicle: Vehicle
    ) : this(id,
            endTime,
            extraTime,
            color,
            isActive,
            Destination(
                    destinationName = destinationName,
                    time = travelTime,
                    vehicle = vehicle
            ),
            days
    )

    var days: DayGroup
        get() = DayGroup(this.daysString)
        set(value) { this.daysString = value.toString() }

    var destinationName: String
        get() = this.destination.destinationName
        set(value) { this.destination.destinationName = value }

    var travelTime: Int
        get() = this.destination.time
        set(value) { this.destination.time = value }

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "destination_id")
    private var destination: Destination

    @DatabaseField(columnName = "days", canBeNull = false)
    private var daysString: String

    @ForeignCollectionField
    lateinit var steps: ForeignCollection<Step>

    val totalTime: Int
        get() = steps.map { it.time }
                .reduce { time1, time2 -> time1 + time2 } + destination.time

    init {
        this.daysString = days.toString()
        this.destination = destination
    }

}