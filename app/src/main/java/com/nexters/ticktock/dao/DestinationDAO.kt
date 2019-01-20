package com.nexters.ticktock.dao

import com.j256.ormlite.dao.Dao
import com.nexters.ticktock.dto.entity.Destination

class DestinationDAO(dao: Dao<Destination, Int>) : BaseDAO<Destination, Int>(dao)