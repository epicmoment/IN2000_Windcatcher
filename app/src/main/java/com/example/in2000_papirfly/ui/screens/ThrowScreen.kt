package com.example.in2000_papirfly.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.R
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.composables.PlaneComposable
import com.example.in2000_papirfly.ui.viewmodels.ThrowScreenState
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModel
import org.osmdroid.util.GeoPoint
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.rememberMapViewWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.views.MapView

@Composable
fun ThrowScreen(
    selectedLocation : GeoPoint,
    locationName: String,
    onLoad: ((map: MapView) -> Unit)? = null,
) {
    // fun Map View() {
    val mapViewState = rememberMapViewWithLifecycle()
    AndroidView(
        { mapViewState },
        Modifier
    ) { mapView -> onLoad?.invoke(mapView) }

    val appContainer = (LocalContext.current.applicationContext as PapirflyApplication).appContainer
    val throwViewModel = remember {
        appContainer.throwViewModelFactory.newViewModel(
            locationName = locationName,
            selectedLocation = selectedLocation,
            mapViewState = mapViewState
        )
    }
    val highscore = throwViewModel.highScore.collectAsState()

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
        Text(text = "Local highscore at ${highscore.value.locationName}: ${highscore.value.distance}km")

        Text(
            text = "Height: ${"%.0f".format(throwViewModel.planeState.collectAsState().value.height)}")


        Button(
            enabled = !throwViewModel.planeState.collectAsState().value.flying,
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
            enabled = !throwViewModel.planeState.collectAsState().value.flying,
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
