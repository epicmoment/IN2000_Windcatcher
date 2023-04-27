package com.example.in2000_papirfly.ui.screens


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.R
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.composables.PlaneComposable
import com.example.in2000_papirfly.ui.viewmodels.ThrowScreenState
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModel
import org.osmdroid.util.GeoPoint
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.rememberMapViewWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ThrowScreen(
    selectedLocation : GeoPoint,
    locationName: String,
    onLoad: ((map: MapView) -> Unit)? = null,
    weatherRepository: DataRepository,
    planeRepository: PlaneRepository,
    onBack: () -> Unit
) {
    // fun Map View() {
    val mapViewState = rememberMapViewWithLifecycle()
    AndroidView(
        { mapViewState },
        Modifier,
    ) { mapView -> onLoad?.invoke(mapView) }

    // TODO
    // I'm making a new ThrowViewModel object here that should be made somewhere else and injected
    val throwViewModel = remember{
        ThrowViewModel(
            locationName,
            selectedLocation,
//            mapViewState,
            { Marker(mapViewState) },
            mapViewState.overlays,
            mapViewState.controller,
            { inputUpdate: () -> Unit
                -> mapViewState.updateOnMoveMap {
                inputUpdate()
                }
            },
            { interactionEnabled: Boolean
                -> mapViewState.setInteraction(interactionEnabled)
            },
            planeRepository = planeRepository,
            weatherRepository = weatherRepository,
        )
    }

    BackHandler {
        Log.d("ThrowScreen", "Back press detected")
        throwViewModel.planeFlying.cancel()
        planeRepository.update(Plane())
        onBack()
    }

    val highScore = throwViewModel.highScore.collectAsState()

    val throwScreenState = throwViewModel.getThrowScreenState()

    // This fixes the map glitching
    mapViewState.controller.setCenter(throwViewModel.previousPlanePos)

    // Wind arrow
    Image(
        painter = painterResource(id = R.drawable.arrow01),
        contentDescription = "TODO",
        modifier = Modifier
            .rotate((throwViewModel.weather.windAngle + 180).toFloat())
            .scale(0.2f)
    )

    // Paper plane
    PlaneComposable(
        planeSize = throwViewModel.getPlaneScale(),
        planeState = throwViewModel.planeState,
        planeVisible = showPlane(throwScreenState)
    )


    Column {
        Text(text = "Wind angle: ${throwViewModel.getWindAngle().toFloat()} - speed: ${"%.2f".format(throwViewModel.getWindSpeed().toFloat())}")
        Text(text = "Plane angle: ${throwViewModel.planeState.collectAsState().value.angle.toFloat()} - speed: ${"%.2f".format(throwViewModel.planeState.collectAsState().value.speed.toFloat())}")
        Text(text = "Plane pos: \n${throwViewModel.planeState.collectAsState().value.pos[0].toFloat()}\n${throwViewModel.planeState.collectAsState().value.pos[1].toFloat()}")
        Text(text = "Local highscore at ${highScore.value.locationName}: ${highScore.value.distance}km")

        Text(
            text = "Height: ${"%.0f".format(throwViewModel.planeState.collectAsState().value.height)}")


        Button(
//            enabled = !throwViewModel.planeState.collectAsState().value.flying,
            enabled = !throwViewModel.flyingState,
            onClick = {
                throwViewModel.throwPlane()
            }
        ){
            Text("Throw")
        }

        var sliderPosition by remember { mutableStateOf(0f) }

        Slider(
            value = sliderPosition,
            onValueChange = {value ->
                throwViewModel.changeAngle(value)
                sliderPosition = value
            },
//            enabled = !throwViewModel.planeState.collectAsState().value.flying,
            enabled = !throwViewModel.flyingState
        )
    }
}

@Composable
fun showPlane(throwScreenState: StateFlow<ThrowScreenState>): Boolean{
    val value = when (throwScreenState.collectAsState().value){
        is ThrowScreenState.Throwing -> true
        is ThrowScreenState.Flying -> true
        is ThrowScreenState.MovingMap -> false
    }
    return value
}
