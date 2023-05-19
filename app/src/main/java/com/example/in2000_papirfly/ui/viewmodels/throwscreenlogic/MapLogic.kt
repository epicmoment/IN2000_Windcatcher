package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.in2000_papirfly.R
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

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

class ThrowPositionMarker(
mapView: MapView,
val openBottomSheet: (Int) -> Unit,
val throwLocation: String
) : Marker(mapView) {

    var setThrowScreenState = {}
    var updateWeather = {}
    var moveLocation = {}
    private var rowPosition = 0

    public override fun onMarkerClickDefault(marker: Marker, mapView: MapView): Boolean {
        updateWeather()
        setThrowScreenState()
        moveLocation()
        openBottomSheet(rowPosition)
        mapView.controller.animateTo(mPosition, 12.0, 1000)
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

class GoalMarker(
    mapView: MapView,
    val throwLocation: String,
    val highScore: Boolean,
    val temporary: Boolean
): Marker(mapView)

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

    // Declares max and min zoom levels
    mapView.maxZoomLevel = 14.0
    mapView.minZoomLevel = 6.0

    return mapView
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