package com.example.in2000_papirfly.data.screenuistates

import com.example.in2000_papirfly.data.components.Weather
import org.osmdroid.util.GeoPoint

data class LogState(

    val isVisible: Boolean = false,
    val distance : Int = 0,
    val newHS : Boolean = false,
    val logPoints : List<LogPoint> = emptyList()

)

data class LogPoint(
    val geoPoint : GeoPoint,
    val weather : Weather,
    val height : Double,
    val speed : Double
)