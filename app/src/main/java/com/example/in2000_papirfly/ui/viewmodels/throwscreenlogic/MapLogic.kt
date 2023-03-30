package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.in2000_papirfly.R
import kotlin.math.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


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

    // Creates a GeoPoint at IFI and adds a marker there
    val IFI = GeoPoint(59.9441, 10.7191)
    val startMarker = Marker(mapView)

    startMarker.position = IFI
    startMarker.title = "Tårnet på IFI!"
    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapView.overlays.add(startMarker)

    // Moves the map to IFI as default
    mapView.controller.setCenter(IFI)

    // Restricts the map view to cover Norway
    mapView.setScrollableAreaLimitLatitude(72.0, 57.5, 0)
    mapView.setScrollableAreaLimitLongitude(3.5, 32.0, 0)

    return mapView
}

/* ChatGPT wrote the following function based on the following prompt:
 * Hi! Can you write me a Kotlin function that takes the following inputs:
 * distance (in kilometers), direction (in degrees), and coordinates (as latitude and longitude),
 * and returns the new point (with latitude and longitude) you would end up at if you went the
 * given direction for the given distance?
 */
fun calculateDestinationPoint(currentPosition: GeoPoint, distance: Double, direction: Double): GeoPoint {
    val R = 6371.0 // Earth's radius in km
    val lat1 = currentPosition.latitude * PI / 180.0 // Convert latitude to radians
    val lon1 = currentPosition.longitude * PI / 180.0 // Convert longitude to radians
    val brng = direction * PI / 180.0 // Convert bearing to radians
    val d = distance / R // Convert distance to angular distance in radians

    val lat2 = asin(sin(lat1) * cos(d) + cos(lat1) * sin(d) * cos(brng))
    val lon2 = lon1 + atan2(sin(brng) * sin(d) * cos(lat1), cos(d) - sin(lat1) * sin(lat2))

    return GeoPoint(lat2 * 180.0 / PI, lon2 * 180.0 / PI) // Convert back to degrees
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

    mapViewState.controller.animateTo(location)
}