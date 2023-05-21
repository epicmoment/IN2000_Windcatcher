package no.met.in2000.windcatcher.data.components

import org.osmdroid.util.GeoPoint

data class HighScore (
    val locationName: String = "",
    val date: Long = 0,
    var distance: Int = 0,
    val flightPath: List<GeoPoint> = listOf(GeoPoint(0.0, 0.0)),
)