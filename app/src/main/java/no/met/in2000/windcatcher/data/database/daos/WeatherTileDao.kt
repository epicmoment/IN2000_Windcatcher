package no.met.in2000.windcatcher.data.database.daos

import androidx.room.*
import no.met.in2000.windcatcher.data.database.entities.WeatherTile

@Dao
interface WeatherTileDao {
    @Query("SELECT * FROM tiles WHERE (loc_x = :locX) AND (loc_y = :locY) LIMIT 1")
    fun getTileAt(locX: Double, locY: Double): WeatherTile?

    @Upsert // THIS ANNOTATION IS A LIFESAVER
    suspend fun insert(tile: WeatherTile)

    @Query("SELECT count(*) FROM tiles")
    fun getSize(): Int
}