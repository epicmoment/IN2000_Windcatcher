package com.example.in2000_papirfly.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.in2000_papirfly.data.database.entities.FlightPathPoint

@Dao
interface FlightPathDao {
    @Query("SELECT * FROM flight_paths WHERE location = :location ORDER BY number")
    fun getFlightPath(location: String): List<FlightPathPoint>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(path: List<FlightPathPoint>)

    @Query("DELETE FROM flight_paths WHERE location = :location")
    fun deleteFLightPath(location: String)
}