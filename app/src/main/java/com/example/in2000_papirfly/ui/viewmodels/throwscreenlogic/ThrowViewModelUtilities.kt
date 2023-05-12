package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.ThrowPointList
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

object ThrowVievModelUtilities {

    fun drawPlanePath(mapOverlay: MutableList<Overlay>, origin: GeoPoint, destination: GeoPoint) {
        val points = listOf(origin, destination)
        val polyline = Polyline()
        polyline.outlinePaint.color = Color.Red.hashCode()
        polyline.setPoints(points)
        mapOverlay.add(polyline)
    }

    fun drawHighScorePath(mapOverlay: MutableList<Overlay>, points: List<GeoPoint>, throwLocation: String) {
        val polyline = PolyLineWithThrowLocation(throwLocation)
        polyline.outlinePaint.color = Color.Yellow.hashCode()
        polyline.setPoints(points)
        mapOverlay.add(polyline)
    }

    fun removeHighScorePath(mapOverlay: MutableList<Overlay>, throwLocation: String) {
        val toRemove = emptyList<Overlay>().toMutableList()

        mapOverlay.forEach { overlay ->
            if (overlay is PolyLineWithThrowLocation) {
                if (overlay.throwLocation == throwLocation) {
                    toRemove.add(overlay)
                }
            } else if (overlay is HighScoreMarker) {
                if (overlay.throwLocation == throwLocation) {
                    toRemove.add(overlay)
                }
            }
        }

        toRemove.forEach { overlay ->
            mapOverlay.remove(overlay)
        }

    }

    fun drawStartMarker(
        markerFactory: (type: String) -> Marker,
        setThrowScreenState: () -> Unit,
        updateWeather: () -> Unit,
        moveLocation: () -> Unit,
        mapOverlay: MutableList<Overlay>,
        startPos: GeoPoint, locationName: String
    ): ThrowPositionMarker {

        val marker: ThrowPositionMarker = markerFactory("Start") as ThrowPositionMarker
        marker.setInfoFromViewModel(setThrowScreenState, updateWeather, moveLocation, ThrowPointList.throwPoints.keys.indexOf(locationName))
        marker.position = startPos
        // This way of getting context works somehow???
        marker.icon = ContextCompat.getDrawable(marker.infoWindow.mapView.context, R.drawable.baseline_push_pin_green_48)
        marker.title = locationName
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapOverlay.add(marker)

        return marker
    }

    fun drawGoalMarker(markerFactory: (type: String) -> Marker, mapOverlay: MutableList<Overlay>, startPos: GeoPoint, markerPos: GeoPoint, newHS: Boolean): Marker {
        val marker = markerFactory("Goal")

        marker.position = markerPos
        marker.icon =
            ContextCompat.getDrawable(marker.infoWindow.mapView.context,
                if (newHS) R.drawable.baseline_push_pin_48_new_hs else R.drawable.baseline_push_pin_48
            )
        marker.title = "${(startPos.distanceToAsDouble(markerPos) / 1000).toInt()}km"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapOverlay.add(marker)
        marker.showInfoWindow()

        return marker
    }
}