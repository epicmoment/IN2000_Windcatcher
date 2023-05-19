package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.components.HighScore
import com.example.in2000_papirfly.data.components.ThrowPointList
import com.example.in2000_papirfly.data.components.Weather
import com.example.in2000_papirfly.data.repositories.FlightPathRepository
import com.example.in2000_papirfly.ui.theme.colBlue
import com.example.in2000_papirfly.ui.theme.colGold
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import kotlin.math.atan2

object ThrowScreenUtilities {

    fun drawPlanePath(mapOverlay: MutableList<Overlay>, origin: GeoPoint, destination: GeoPoint) {
        val points = listOf(origin, destination)
        val polyline = Polyline()
        polyline.outlinePaint.color = colBlue.hashCode()
        polyline.setPoints(points)
        mapOverlay.add(polyline)
    }

    fun drawHighScorePath(mapOverlay: MutableList<Overlay>, points: List<GeoPoint>, throwLocation: String) {
        val polyline = PolyLineWithThrowLocation(throwLocation)
        polyline.outlinePaint.color = colGold.hashCode()
        polyline.setPoints(points)
        mapOverlay.add(polyline)
    }

    fun changeHighScoreMarkerToNormal(
        markerFactory: (type: String, throwLocation: String, temporary: Boolean) -> Marker,
        mapOverlay: MutableList<Overlay>,
        startPos: GeoPoint,
        throwLocation: String,
    ) {
        val oldAndNewMarker: MutableList<Marker> = mutableListOf()
        for (marker in FlightPathRepository.markers) {
            if (marker is GoalMarker && marker.highScore && marker.throwLocation == throwLocation) {

                oldAndNewMarker.add(marker)

                mapOverlay.remove(marker)

                oldAndNewMarker.add(
                    drawGoalMarker(
                        markerFactory,
                        mapOverlay,
                        startPos,
                        throwLocation,
                        marker.position,
                        newHS = false,
                        temporary = false
                    )
                )
                break
            }
        }

        if (oldAndNewMarker.size == 2) {
            FlightPathRepository.markers.add(oldAndNewMarker.last())
            FlightPathRepository.markers.remove(oldAndNewMarker.first())
        }
    }

    fun removeHighScorePath(mapOverlay: MutableList<Overlay>, throwLocation: String) {
        val toRemove = emptyList<Overlay>().toMutableList()

        mapOverlay.forEach { overlay ->
            if (overlay is PolyLineWithThrowLocation) {
                if (overlay.throwLocation == throwLocation) {
                    toRemove.add(overlay)
                }
            } else if (overlay is GoalMarker) {
                overlay.closeInfoWindow()
                if (overlay.throwLocation == throwLocation && overlay.temporary) {
                    toRemove.add(overlay)
                }
            }
        }

        toRemove.forEach { overlay ->
            mapOverlay.remove(overlay)
        }

    }

    fun drawStartMarker(
        markerFactory: (type: String, throwLocation: String, temporary: Boolean) -> Marker,
        setThrowScreenState: () -> Unit,
        updateWeather: () -> Unit,
        moveLocation: () -> Unit,
        mapOverlay: MutableList<Overlay>,
        startPos: GeoPoint,
        locationName: String,
    ): ThrowPositionMarker {

        val marker: ThrowPositionMarker = markerFactory(
            "Start",
            locationName,
            false
        ) as ThrowPositionMarker

        marker.setInfoFromViewModel(
            setThrowScreenState, updateWeather,
            moveLocation,
            ThrowPointList.throwPoints.keys.indexOf(locationName)
        )
        marker.position = startPos
        marker.icon = ContextCompat.getDrawable(
            marker.infoWindow.mapView.context,
            R.drawable.pin_throwpoint
        )
        marker.title = locationName
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapOverlay.add(marker)

        return marker
    }

    fun drawGoalMarker(
        markerFactory: (type: String, throwLocation: String, temporary: Boolean) -> Marker,
        mapOverlay: MutableList<Overlay>,
        startPos: GeoPoint,
        throwLocation: String,
        markerPos: GeoPoint,
        newHS: Boolean,
        temporary: Boolean
    ): GoalMarker {
        val marker = markerFactory(
            if (newHS) "HighScore" else "Goal",
            throwLocation,
            temporary
        ) as GoalMarker

        Log.d("MARKER", "Drawing goal marker")

        marker.position = markerPos
        marker.icon =
            ContextCompat.getDrawable(marker.infoWindow.mapView.context,
                if (newHS) R.drawable.pin_highscore else R.drawable.pin_destination
            )
        marker.title = "${(startPos.distanceToAsDouble(markerPos) / 1000).toInt()}km"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapOverlay.add(marker)
        marker.showInfoWindow()

        return marker
    }

    /**
     * This method produces a map where the keys are the names of the throw point locations,
     * and the values are all empty 'HighScore'-objects.
     *
     * @return The mutable map as described above
     */
    fun emptyHighScoreMap(): MutableMap<String, HighScore> {
        val map = emptyMap<String, HighScore>().toMutableMap()
        ThrowPointList.throwPoints.forEach {
            map[it.key] = HighScore(locationName = it.key)
        }
        return map
    }

    /**
     * This method produces a map where the keys are the names of the throw point locations,
     * and the values are all empty 'Weather'-objects.
     *
     * @return The mutable map as described above
     */
    fun emptyThrowPointWeatherList(): List<Weather> {
        val weather = emptyList<Weather>().toMutableList()
        ThrowPointList.throwPoints.forEach {
            weather.add(
                Weather(
                    namePos = it.key
                )
            )
        }
        return weather
    }

    /**
     * This method produces a map for keeping track of which high score paths
     * currently shown on the map screen. The keys are the names of the throw
     * point locations, and the values are all initialized to 'false'.
     *
     * @return The mutable map as described above
     */
    fun defaultHighScoreShownMap(): MutableMap<String, Boolean> {
        val highScoreShownMap = emptyMap<String, Boolean>().toMutableMap()
        ThrowPointList.throwPoints.forEach {
            highScoreShownMap[it.key] = false
        }
        return highScoreShownMap
    }

    fun getRotationAngle(currentPosition: Offset, center: Offset): Double {
        val (dx, dy) = currentPosition - center
        val theta = atan2(dy, dx).toDouble()

        var angle = Math.toDegrees(theta)

        if (angle < 0) {
            angle += 360.0
        }
        return angle
    }
}