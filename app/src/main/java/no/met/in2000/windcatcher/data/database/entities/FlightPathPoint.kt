package no.met.in2000.windcatcher.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "flight_paths",
    primaryKeys = ["location", "number"],
    foreignKeys = [
        ForeignKey(
            entity = ThrowPoint::class,
            parentColumns = arrayOf("name"),
            childColumns = arrayOf("location"),
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        )
    ]
)
data class FlightPathPoint(
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "loc_x") val locX: Double,
    @ColumnInfo(name = "loc_y") val locY: Double,
    @ColumnInfo(name = "info") val info: String?
)
