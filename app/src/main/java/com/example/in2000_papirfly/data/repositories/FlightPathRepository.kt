package com.example.in2000_papirfly.data.repositories

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

object FlightPathRepository {
    val flightPaths: MutableList<Pair<Int, MutableList<GeoPoint>>> = mutableListOf()
    val markers: MutableList<Marker> = mutableListOf()
}