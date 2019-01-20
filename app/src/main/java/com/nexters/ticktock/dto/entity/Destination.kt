package com.nexters.ticktock.dto.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "destinations")
class Destination(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "destination_name", canBeNull = false)
        var destinationName: String,

        @DatabaseField(columnName = "time", canBeNull = false)
        var time: Int

){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Destination
        if (id != other.id) return false
        if (destinationName != other.destinationName) return false
        if (time != other.time) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + destinationName.hashCode()
        result = 31 * result + time
        return result
    }
}