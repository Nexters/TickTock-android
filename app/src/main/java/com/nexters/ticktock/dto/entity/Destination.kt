package com.nexters.ticktock.dto.entity

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.nexters.ticktock.dto.Vehicle

@DatabaseTable(tableName = "destinations")
class Destination(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "destination_name", canBeNull = false)
        var destinationName: String,

        @DatabaseField(columnName = "vehicle", canBeNull = false)
        var vehicle: Vehicle,

        @DatabaseField(columnName = "time", canBeNull = false)
        var time: Int

)