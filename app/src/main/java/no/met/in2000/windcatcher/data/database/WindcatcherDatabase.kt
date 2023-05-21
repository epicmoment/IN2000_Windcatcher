package no.met.in2000.windcatcher.data.database

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import no.met.in2000.windcatcher.data.database.daos.FlightPathDao
import no.met.in2000.windcatcher.data.database.daos.ThrowPointDao
import no.met.in2000.windcatcher.data.database.daos.WeatherTileDao
import no.met.in2000.windcatcher.data.database.entities.FlightPathPoint
import no.met.in2000.windcatcher.data.database.entities.ThrowPoint
import no.met.in2000.windcatcher.data.database.entities.WeatherTile

@Database(
    entities = [
        WeatherTile::class,
        ThrowPoint::class,
        FlightPathPoint::class
    ],
    version = 1,
    exportSchema = true
)
abstract class WindcatcherDatabase: RoomDatabase() {
    abstract fun weatherTileDao(): WeatherTileDao
    abstract fun throwPointDao(): ThrowPointDao
    abstract fun flightPathDao(): FlightPathDao
    init {
        Log.i("Database", "Database instance created")
    }
}