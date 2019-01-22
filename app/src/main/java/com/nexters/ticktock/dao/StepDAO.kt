package com.nexters.ticktock.dao

import com.j256.ormlite.dao.Dao
import com.nexters.ticktock.dto.entity.Step

class StepDAO(dao: Dao<Step, Int>) : BaseDAO<Step, Int>(dao)