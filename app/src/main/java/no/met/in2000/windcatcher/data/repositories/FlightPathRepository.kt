package no.met.in2000.windcatcher.data.repositories

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

object FlightPathRepository {
    val flightPaths: MutableList<Pair<Int, MutableList<GeoPoint>>> = mutableListOf()
    val markers: MutableList<Marker> = mutableListOf()
}