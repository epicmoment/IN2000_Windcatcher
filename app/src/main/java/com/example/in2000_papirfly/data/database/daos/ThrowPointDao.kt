package com.example.in2000_papirfly.data.database.daos

import androidx.room.*
import com.example.in2000_papirfly.data.database.entities.ThrowPoint

@Dao
interface ThrowPointDao {
    @Query("SELECT * FROM throw_points WHERE name = :location LIMIT 1")
    fun getThrowPointInfo(location: String): ThrowPoint?

    @Upsert
    suspend fun insert(location: ThrowPoint)

    @Query("SELECT count(*) FROM throw_points")
    fun getSize(): Int
}