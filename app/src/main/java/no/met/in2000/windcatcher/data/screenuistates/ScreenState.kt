package no.met.in2000.windcatcher.data.screenuistates

import org.osmdroid.util.GeoPoint

data class ScreenState(
    val location: GeoPoint = GeoPoint(59.944030, 10.719282),
    val locationName: String = "Oslo"
)
