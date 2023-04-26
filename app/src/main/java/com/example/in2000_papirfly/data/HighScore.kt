package com.example.in2000_papirfly.data

import org.osmdroid.util.GeoPoint

data class HighScore (
    val locationName: String = "",
    val date: Long? = null,
    var distance: Int? = null,
    val flightPath: List<GeoPoint>? = null
)