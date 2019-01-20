package com.nexters.ticktock.dao

import com.j256.ormlite.dao.Dao
import com.nexters.ticktock.dto.entity.StepReport

class StepReportDAO(dao: Dao<StepReport, Int>) : BaseDAO<StepReport, Int>(dao)