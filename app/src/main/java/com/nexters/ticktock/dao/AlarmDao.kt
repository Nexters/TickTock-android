package com.nexters.ticktock.dao

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.nexters.ticktock.dao.converter.DayConverter
import com.nexters.ticktock.dao.converter.TickTockColorConverter
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.model.Step
import com.nexters.ticktock.utils.Time

class AlarmDao(private val context: Context) {


    private val dayConverter = DayConverter()
    private val colorConverter = TickTockColorConverter()

    companion object {
        private const val LOG_TAG = "AlarmDao"

        private val SELECT_ALL_ALARM = """
            SELECT
                *
            FROM
                alarm a
            LEFT JOIN
                step s ON a.id = s.alarm_id
        """.trimIndent()

        private val SELECT_ALL_ALARM_WHERE = """
            $SELECT_ALL_ALARM
            WHERE
                a.id = ?
        """.trimIndent()

        private val DELETE_ALL_ALARM = """
            DELETE FROM alarm
        """.trimIndent()

        private val DELETE_ALARM_WHERE = """
            $DELETE_ALL_ALARM
            WHERE id = ?
        """.trimIndent()

        private val INSERT_ALARM = """
            INSERT INTO alarm (days, title, start_location, end_location, color, enable, end_time, travel_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        private val INSERT_STEP = """
            INSERT INTO step (name, `order`, duration, alarm_id)
            VALUES (?, ?, ?, ?)
        """.trimIndent()

        private val UPDATE_ALARM = """
            UPDATE alarm
            SET days = ?,
                title = ?,
                start_location = ?,
                end_location = ?,
                color = ?,
                enable = ?,
                end_time = ?,
                travel_time = ?
            WHERE id = ?
        """.trimIndent()

        private val UPDATE_STEP = """
            UPDATE step
            SET name = ?,
                `order` = ?,
                duration = ?,
                alarm_id = ?
            WHERE id = ?
        """.trimIndent()

        private val MAX_ALARM_ID = """
            SELECT
	            MAX(id)
            FROM
	            alarm
        """.trimIndent()

        private val COUNT_ALARM = """
            SELECT
                COUNT(id)
            FROM
                alarm
        """.trimIndent()
    }

    fun getAlarmCount(): Long {
        Log.d(LOG_TAG, "getAlarmCount called")
        val readableDB = TickTockDBHelper.getInstance(context).readableDatabase

        return readableDB.rawQuery(COUNT_ALARM, null).use { it.getLong(0) }
    }

    fun findAll(): List<Alarm> {
        Log.d(LOG_TAG, "findAll called")

        val readableDB = TickTockDBHelper.getInstance(context).readableDatabase
        val cursor = readableDB.rawQuery(SELECT_ALL_ALARM, null)

        val alarmMap = mutableMapOf<Long, Alarm>()
        val stepSet = mutableSetOf<Step>()

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    it.makePair().also {pair ->
                        alarmMap[pair.first.id] = pair.first
                        pair.second?.let { it1 -> stepSet.add(it1) }
                    }
                } while (it.moveToNext())
            }
        }

        val alarmList = stepSet.groupBy { it.alarmId }
                .map {
                    alarmMap[it.key]?.apply {
                        steps.addAll(it.value)
                    }
                }.filterNotNull()

        return if (alarmList.isEmpty()) {
            alarmMap.values.toList()
        } else {
            alarmList
        }
    }

    fun findById(id: Long): Alarm? {
        Log.d(LOG_TAG, "findById called - param: $id")
        val readableDB = TickTockDBHelper.getInstance(context).readableDatabase
        val cursor = readableDB.rawQuery(SELECT_ALL_ALARM_WHERE, arrayOf(id.toString()))

        var alarm: Alarm? = null
        val stepSet = mutableSetOf<Step>()

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    it.makePair().also {pair ->
                        alarm = pair.first
                        pair.second?.let { it1 -> stepSet.add(it1) }
                    }
                } while (it.moveToNext())
            }
        }

        return alarm?.apply { steps = stepSet }.also {
            Log.d(LOG_TAG, "findById result: $it")
        }
    }

    fun save(alarm: Alarm) {
        Log.d(LOG_TAG, "save called - param: $alarm")

        if (alarm.id != 0L) {
            update(alarm)
        } else {
            insert(alarm)
        }
    }

    private fun insert(alarm: Alarm) {
        Log.d(LOG_TAG, "insert called - param: $alarm")

        val writableDB = TickTockDBHelper.getInstance(context).writableDatabase
        val readableDB = TickTockDBHelper.getInstance(context).readableDatabase

        writableDB.execSQL(INSERT_ALARM, arrayOf(
                alarm.days.let { dayConverter.toDatabase(it) },
                alarm.title,
                alarm.startLocation,
                alarm.endLocation,
                alarm.color.name,
                alarm.enable.let { if (it) 1 else 0 },
                alarm.endTime.time,
                alarm.travelTime.time
        ))

        val alarmId = readableDB.rawQuery(MAX_ALARM_ID, null).use {
            it.moveToFirst()
            it.getLong(0)
        }
        val steps = alarm.steps

        steps.forEach {
            writableDB.execSQL(INSERT_STEP, arrayOf(
                    it.name,
                    it.order,
                    it.duration,
                    alarmId
            ))
        }
    }

    private fun update(alarm: Alarm) {
        Log.d(LOG_TAG, "update called - param: $alarm")

        val writableDB = TickTockDBHelper.getInstance(context).writableDatabase
        writableDB.execSQL(UPDATE_ALARM, arrayOf(
                alarm.days.let { dayConverter.toDatabase(it) },
                alarm.title,
                alarm.startLocation,
                alarm.endLocation,
                alarm.color.name,
                alarm.enable.let { if (it) 1 else 0 },
                alarm.endTime.time,
                alarm.travelTime.time,
                alarm.id
        ))

        alarm.steps.forEach {
            writableDB.execSQL(UPDATE_STEP, arrayOf(
                    it.name,
                    it.order,
                    it.duration,
                    alarm.id,
                    it.id
            ))
        }
    }

    fun delete(alarm: Alarm) {
        Log.d(LOG_TAG, "delete called - param: $alarm")

        val writableDB = TickTockDBHelper.getInstance(context).writableDatabase
        writableDB.execSQL(DELETE_ALARM_WHERE, arrayOf(alarm.id))
    }

    fun deleteAll() {
        Log.d(LOG_TAG, "deleteAll called")

        val writableDB = TickTockDBHelper.getInstance(context).writableDatabase
        writableDB.execSQL(DELETE_ALL_ALARM)
    }

    private fun Cursor.makePair(): Pair<Alarm, Step?> {
        val alarm = Alarm(
                id = getLong(0),
                days = getString(1).let { dayConverter.fromDatabase(it) },
                title = getString(2),
                startLocation = getString(3),
                endLocation = getString(4),
                color = getString(5).let { colorConverter.fromDatabase(it) },
                enable = getInt(6) != 0,
                endTime = Time(getInt(7)),
                travelTime = Time(getInt(8))
        )

        if (getLong(9) == 0L) {
            return alarm to null
        }

        val step = Step(
                id = getLong(9),
                name = getString(10),
                order = getInt(11),
                duration = Time(getInt(12)),
                alarmId = getLong(13)
        )

        return alarm to step
    }
}