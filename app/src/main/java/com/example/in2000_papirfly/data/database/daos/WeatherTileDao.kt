package com.example.in2000_papirfly.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.in2000_papirfly.data.database.entities.WeatherTile

@Dao
interface WeatherTileDao {
    @Query("SELECT * FROM tiles WHERE (loc_x = :locX) AND (loc_y = :locY) LIMIT 1")
    fun getTileAt(locX: Int, locY: Int): WeatherTile

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tile: WeatherTile)
}