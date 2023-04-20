package com.example.in2000_papirfly.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.in2000_papirfly.data.database.entities.ThrowPoint

@Dao
interface ThrowPointDao {
    @Query("SELECT * FROM throw_points WHERE name = :location LIMIT 1")
    fun getThrowPointInfo(location: String): ThrowPoint

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: ThrowPoint)

    @Query("SELECT count(*) FROM throw_points")
    fun getSize(): Int
}