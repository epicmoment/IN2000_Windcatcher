package com.example.in2000_papirfly.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tiles",
    primaryKeys = ["loc_x", "loc_y"]
)
data class WeatherTile(
    @ColumnInfo(name = "loc_x") val locX: Int,
    @ColumnInfo(name = "loc_y") val locY: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long,
    @ColumnInfo(name = "cloud_cover") val cloudCover: String,
    @ColumnInfo(name = "precipitation") val precipitation: Double,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "wind_direction") val windDirection: Double,
    @ColumnInfo(name = "wind_gusts") val windGusts: Double,
)