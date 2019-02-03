package com.nexters.ticktock.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.nexters.ticktock.dto.entity.*

class DatabaseHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DatabaseHelper.DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ticktock.db"
        const val DATABASE_VERSION = 1
        val className = DatabaseHelper::class.java.canonicalName!!
    }

    val articleDAO: ArticleDAO by lazy { ArticleDAO(getDao(Article::class.java)) }
    val destinationDAO: DestinationDAO by lazy { DestinationDAO(getDao(Destination::class.java)) }
    val stepDAO: StepDAO by lazy { StepDAO(getDao(Step::class.java)) }
    val templateDAO: TemplateDAO by lazy { TemplateDAO(getDao(Template::class.java)) }

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {

        TableUtils.createTable(connectionSource, Article::class.java)
        Log.i(className, "onCreate - Article")

        TableUtils.createTable(connectionSource, Destination::class.java)
        Log.i(className, "onCreate - Destination")

        TableUtils.createTable(connectionSource, Step::class.java)
        Log.i(className, "onCreate - Step")

        TableUtils.createTable(connectionSource, Preset::class.java)
        Log.i(className, "onCreate - Preset")

        TableUtils.createTable(connectionSource, Template::class.java)
        Log.i(className, "onCreate - Template")
    }

    override fun onUpgrade(database: SQLiteDatabase?, connectionSource: ConnectionSource?, oldVersion: Int, newVersion: Int) {
        // do nothing
    }
}


