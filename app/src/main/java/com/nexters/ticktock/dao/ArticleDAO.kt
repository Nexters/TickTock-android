package com.nexters.ticktock.dao

import com.j256.ormlite.dao.Dao
import com.nexters.ticktock.dto.entity.Article

class ArticleDAO(dao: Dao<Article, Int>) : BaseDAO<Article, Int>(dao)