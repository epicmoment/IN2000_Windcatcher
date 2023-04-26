package com.example.in2000_papirfly.data.database.daos

import androidx.room.*
import com.example.in2000_papirfly.data.database.entities.FlightPathPoint

@Dao
interface FlightPathDao {
    @Query("SELECT * FROM flight_paths WHERE location = :location ORDER BY number")
    fun getFlightPath(location: String): MutableList<FlightPathPoint>

    @Upsert
    suspend fun insert(point: FlightPathPoint)

    @Query("DELETE FROM flight_paths WHERE location = :location")
    fun deleteFLightPath(location: String)

    @Query("SELECT count(*) FROM flight_paths")
    fun getSize(): Int
}