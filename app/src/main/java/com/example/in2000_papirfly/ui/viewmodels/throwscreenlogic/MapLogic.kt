package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic


import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.in2000_papirfly.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


//Entire code based on this: gist.github.com/ArnyminerZ/418683e3ef43ccf1268f9f62940441b1
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
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
    mapView.minZoomLevel = 8.0
    mapView.controller.setZoom(18.0)

    // Restricts the map view to cover Norway
    mapView.setScrollableAreaLimitLatitude(72.0, 57.5, 0)
    mapView.setScrollableAreaLimitLongitude(3.5, 32.0, 0)

    return mapView
}

fun lockMapToPosition(mapViewState: MapView, position: GeoPoint) {
    mapViewState.setScrollableAreaLimitLatitude(position.latitude, position.latitude, 0)
    mapViewState.setScrollableAreaLimitLongitude(position.longitude, position.longitude, 0)
}

fun unlockMap(mapViewState: MapView) {
    mapViewState.setScrollableAreaLimitLatitude(72.0, 57.5, 0)
    mapViewState.setScrollableAreaLimitLongitude(3.5, 32.0, 0)
}

fun drawPlanePath(mapViewState: MapView, origin: GeoPoint, destination: GeoPoint) {
    val points = listOf(origin, destination)
    val polyline = Polyline() //TODO
    polyline.outlinePaint.color = Color.RED
    polyline.setPoints(points)
    mapViewState.overlays.add(polyline)
}

fun drawStartMarker(mapViewState: MapView, startPos: GeoPoint) {
    val marker = Marker(mapViewState)
    marker.position = startPos
    marker.icon = ContextCompat.getDrawable(mapViewState.context, R.drawable.baseline_push_pin_green_48)
    marker.title = "Start"
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapViewState.overlays.add(marker)
}

fun drawGoalMarker(mapViewState: MapView, startPos: GeoPoint, markerPos: GeoPoint) {
    val marker = Marker(mapViewState)

    marker.position = markerPos
    marker.icon = ContextCompat.getDrawable(mapViewState.context, R.drawable.baseline_push_pin_48)
    marker.title = "${(startPos.distanceToAsDouble(markerPos) / 1000).toInt()}km"
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapViewState.overlays.add(marker)
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


@Composable
fun MapView(
    modifier: Modifier = Modifier,
    onLoad: ((map: MapView) -> Unit)? = null,
    location: GeoPoint
) {
    val mapViewState = rememberMapViewWithLifecycle()

    AndroidView(
        { mapViewState },
        modifier
    ) { mapView -> onLoad?.invoke(mapView) }

//    mapViewState.controller.animateTo(location)
}