package com.example.in2000_papirfly.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.in2000_papirfly.data.database.daos.FlightPathDao
import com.example.in2000_papirfly.data.database.daos.ThrowPointDao
import com.example.in2000_papirfly.data.database.daos.WeatherTileDao
import com.example.in2000_papirfly.data.database.entities.FlightPathPoint
import com.example.in2000_papirfly.data.database.entities.ThrowPoint
import com.example.in2000_papirfly.data.database.entities.WeatherTile

@Database(
    entities = [
        WeatherTile::class,
        ThrowPoint::class,
        FlightPathPoint::class
    ],
    version = 1,
    exportSchema = true
)
abstract class PapirflyDatabase: RoomDatabase() {
    abstract fun weatherTileDao(): WeatherTileDao
    abstract fun throwPointDao(): ThrowPointDao
    abstract fun flightPathDao(): FlightPathDao
    init {
        Log.i("Database", "Database instance created")
    }
}