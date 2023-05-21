package no.met.in2000.windcatcher.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "throw_points",
    foreignKeys = [
        ForeignKey(
            entity = WeatherTile::class,
            parentColumns = arrayOf("loc_x", "loc_y"),
            childColumns = arrayOf("tile_x", "tile_y"),
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        )
    ]
)
data class ThrowPoint(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "tile_x") val tileX: Double,
    @ColumnInfo(name = "tile_y") val tileY: Double,
    @ColumnInfo(name = "highscore_date") val hSDate: Long = 0,
    @ColumnInfo(name = "highscore_distance") val hSDistance: Int = 0
)