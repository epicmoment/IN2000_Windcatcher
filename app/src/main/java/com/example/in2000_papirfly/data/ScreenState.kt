package com.example.in2000_papirfly.data

import org.osmdroid.util.GeoPoint

data class ScreenState(
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val locationName: String = ""
)
