package com.nexters.ticktock.dto.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = "step_reports")
class StepReport(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "time", canBeNull = false)
        var time: Int,

        @DatabaseField(columnName = "reportTime", canBeNull = false)
        var reportTime: Date

){

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "step_id")
    lateinit var step: Step

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as StepReport
        if (id != other.id) return false
        if (time != other.time) return false
        if (reportTime != other.reportTime) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + time
        result = 31 * result + reportTime.hashCode()
        return result
    }

}