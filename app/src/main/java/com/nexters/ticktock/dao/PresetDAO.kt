package com.nexters.ticktock.dao

import com.j256.ormlite.dao.Dao
import com.nexters.ticktock.dto.entity.Preset

class PresetDAO(dao: Dao<Preset, Int>) : BaseDAO<Preset, Int>(dao)