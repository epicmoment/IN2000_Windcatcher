package com.example.in2000_papirfly.data.database.entities

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
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FlightPathPoint(
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "loc_x") val locX: Int,
    @ColumnInfo(name = "loc_y") val locY: Int,
    @ColumnInfo(name = "info") val info: String?
)
