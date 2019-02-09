package com.nexters.ticktock.dto.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "presets")
class Preset(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "sequence", canBeNull = false)
        var sequence: Int,

        @DatabaseField(columnName = "name", canBeNull = false)
        var name: String,

        @DatabaseField(columnName = "time", canBeNull = false)
        var time: Int

){

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "template_id")
    lateinit var template: Template

    fun toStep(): Step =
            Step(
                    name = this.name,
                    sequence = this.sequence,
                    time = this.time
            )
}
