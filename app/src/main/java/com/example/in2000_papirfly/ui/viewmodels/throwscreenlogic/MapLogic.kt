package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.in2000_papirfly.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.compose.ui.graphics.Color
import com.example.in2000_papirfly.data.ThrowPointList
import com.example.in2000_papirfly.ui.theme.colGold
import com.example.in2000_papirfly.ui.theme.colRed
import org.osmdroid.views.overlay.Overlay

// This class based on comment by grine4ka:
// https://stackoverflow.com/a/60808815/18814731
class DisableMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MapView(context, attrs) {
    private var isInteractionEnabled = true
    private var onMoveMap: () -> Unit = {}

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (!isInteractionEnabled) {
            return false
        }
        onMoveMap()
        return super.dispatchTouchEvent(event)
    }

    fun updateOnMoveMap(newOnMoveMap: () -> Unit){
        onMoveMap = newOnMoveMap
    }

    // This makes it possible to disable scrolling and zooming
    fun setInteraction(isEnabled: Boolean) {
        isInteractionEnabled = isEnabled
    }
}

class ThrowPositionMarker constructor(
mapView: MapView, val openBottomSheet: (Int) -> Unit
) : Marker(mapView) {

    var setThrowScreenState = {}
    var updateWeather = {}
    var moveLocation = {}
    var rowPosition = 0

    public override fun onMarkerClickDefault(marker: Marker?, mapView: MapView?): Boolean {
        updateWeather()
        setThrowScreenState()
        moveLocation()
        openBottomSheet(rowPosition)
        mapView!!.controller.animateTo(mPosition, 12.0, 1000)
        showInfoWindow()
        return true
    }

    fun setInfoFromViewModel(setThrowScreenState: () -> Unit, updateWeather: () -> Unit, moveLocation: () -> Unit, rowPosition: Int) {
        this.setThrowScreenState = setThrowScreenState
        this.updateWeather = updateWeather
        this.moveLocation = moveLocation
        this.rowPosition = rowPosition
    }
}

class HighScoreMarker(mapView: MapView, val throwLocation: String): Marker(mapView)

class PolyLineWithThrowLocation(val throwLocation: String): Polyline()

// OSMDroid as composable based on:
// gist.github.com/ArnyminerZ/418683e3ef43ccf1268f9f62940441b1
@Composable
fun rememberMapViewWithLifecycle(): DisableMapView {
    val context = LocalContext.current
    val mapView = remember {
        DisableMapView(context).apply {
            id = R.id.map
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    // Activates pinch-to-zoom
    mapView.setMultiTouchControls(true)
    // Hides zoom buttons
    mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

    // Declares max and min zoom levels, and sets default zoom level
    // maxZoomLevel 12 is good for watching the plane glide

    mapView.maxZoomLevel = 12.0
    mapView.minZoomLevel = 6.0

//    val filter = androidx.compose.ui.graphics.ColorFilter
//    mapView.overlayManager.tilesOverlay.setColorFilter(filter.lighting(Color(0x50A9AAFF), Color.Unspecified).asAndroidColorFilter())

    return mapView
}

fun drawPlanePath(mapOverlay: MutableList<Overlay>, origin: GeoPoint, destination: GeoPoint) {
    val points = listOf(origin, destination)
    val polyline = Polyline() //TODO
    polyline.outlinePaint.color = colRed.hashCode()
    polyline.setPoints(points)
    mapOverlay.add(polyline)
}

fun drawHighScorePath(mapOverlay: MutableList<Overlay>, points: List<GeoPoint>, throwLocation: String) {
    val polyline = PolyLineWithThrowLocation(throwLocation) //TODO
    polyline.outlinePaint.color = colGold.hashCode()
    polyline.setPoints(points)
    mapOverlay.add(polyline)
}

fun removeHighScorePath(mapOverlay: MutableList<Overlay>, throwLocation: String) {
    mapOverlay.forEach {overlay ->
        if (overlay is PolyLineWithThrowLocation) {
            if (overlay.throwLocation == throwLocation) {
                mapOverlay.remove(overlay)
            }
        } else if (overlay is HighScoreMarker) {
            if (overlay.throwLocation == throwLocation) {
                mapOverlay.remove(overlay)
            }
        }
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
    marker.icon = ContextCompat.getDrawable(marker.infoWindow.mapView.context, R.drawable.pin_debug1)
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
        if (newHS) R.drawable.pin_highscore else R.drawable.pin_destination
    )
    marker.title = "${(startPos.distanceToAsDouble(markerPos) / 1000).toInt()}km"
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapOverlay.add(marker)
    marker.showInfoWindow()

    return marker
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            else -> {}
        }
    }
}