package no.met.in2000.windcatcher.data.screenuistates

import no.met.in2000.windcatcher.data.components.Weather
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