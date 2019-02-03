package com.nexters.ticktock.dao

import com.j256.ormlite.dao.Dao
import com.nexters.ticktock.dto.entity.Template

class TemplateDAO(dao: Dao<Template, Int>) : BaseDAO<Template, Int>(dao)