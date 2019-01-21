package com.nexters.ticktock.dto.entity

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "steps")
class Step(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "sequence", canBeNull = false)
        var sequence: Int,

        @DatabaseField(columnName = "name", canBeNull = false)
        var name: String,

        @DatabaseField(columnName = "time", canBeNull = false)
        var time: Int

){

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "article_id")
    lateinit var article: Article

    @ForeignCollectionField
    lateinit var reports: ForeignCollection<StepReport>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Step
        if (id != other.id) return false
        if (sequence != other.sequence) return false
        if (name != other.name) return false
        if (time != other.time) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + sequence
        result = 31 * result + name.hashCode()
        result = 31 * result + time
        return result
    }

}
