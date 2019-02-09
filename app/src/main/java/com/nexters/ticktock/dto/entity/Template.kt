package com.nexters.ticktock.dto.entity

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "templates")
class Template(

        @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
        var id: Int = 0,

        @DatabaseField(columnName = "name", canBeNull = false)
        var name: String
) {

    @ForeignCollectionField
    lateinit var presets: ForeignCollection<Preset>

}