package com.nexters.ticktock.dao

import com.j256.ormlite.dao.Dao

abstract class BaseDAO<T : Any, ID>(
        private val dao: Dao<T, ID>
){
    fun findById(id: ID) =
            dao.queryForId(id)

    fun findAll() =
            dao.queryForAll()

    fun save(entity: T){
        dao.createOrUpdate(entity)
        dao.refresh(entity)
    }

    fun saveAll(entities: Iterable<T>){
        entities.forEach(this::save)
    }

    fun delete(entity: T){
        dao.delete(entity)
    }
}