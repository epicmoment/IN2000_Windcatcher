package no.met.in2000.windcatcher.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "tiles",
    primaryKeys = ["loc_x", "loc_y"]
)
data class WeatherTile(
    @ColumnInfo(name = "loc_x") val locX: Double,
    @ColumnInfo(name = "loc_y") val locY: Double,
    @ColumnInfo(name = "last_updated_nc") val lastUpdatedNC: Long,
    @ColumnInfo(name = "last_updated_lf") val lastUpdatedLF: Long,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "air_pressure") val airPressure: Double,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "precipitation") val precipitation: Double,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "wind_direction") val windDirection: Double,
//    @ColumnInfo(name = "wind_gusts") val windGusts: Double,
)