package com.example.in2000_papirfly.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint

@Entity(
    tableName = "throw_points",
    foreignKeys = [
        ForeignKey(
            entity = WeatherTile::class,
            parentColumns = arrayOf("loc_x", "loc_y"),
            childColumns = arrayOf("tile_x", "tile_y"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ThrowPoint(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "tile_x") val tileX: Double,
    @ColumnInfo(name = "tile_y") val tileY: Double,
    @ColumnInfo(name = "highscore_date") val hSDate: Long?,
    @ColumnInfo(name = "highscore_distance") val hSDistance: Int?
)