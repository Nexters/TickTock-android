package com.nexters.ticktock.dto.entity

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import com.nexters.ticktock.dto.DayGroup
import java.util.*

@DatabaseTable(tableName = "articles")
class Article(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "title", canBeNull = false)
        var title: String,

        @DatabaseField(columnName = "end_time", canBeNull = false)
        var endTime: Date,

        @DatabaseField(columnName = "extra_time", canBeNull = false)
        var extraTime: Int,

        destination: Destination,

        days: DayGroup

){

    constructor(id: Int = 0, title: String, endTime: Date, extraTime: Int, destinationName: String, travelTime: Int, days: DayGroup)
            : this(id, title, endTime, extraTime, Destination(destinationName = destinationName, time = travelTime), days)

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false
        if (title != other.title) return false
        if (endTime != other.endTime) return false
        if (extraTime != other.extraTime) return false
        if (daysString != other.daysString) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + extraTime
        result = 31 * result + daysString.hashCode()
        return result
    }

}