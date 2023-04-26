package com.example.in2000_papirfly.ui.screens


import android.annotation.SuppressLint
import android.util.Half.toFloat
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
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.R
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.plane.WeatherRepository
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModel
import org.osmdroid.util.GeoPoint
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.rememberMapViewWithLifecycle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.views.MapView


@Composable
fun ThrowScreen(
    selectedLocation : GeoPoint,
    onLoad: ((map: MapView) -> Unit)? = null,
    getWeather: (location: String) -> Weather,
    weatherRepository: WeatherRepositoryMVP,
    planeRepository: PlaneRepository
) {

    // fun Map View() {
    val mapViewState = rememberMapViewWithLifecycle()
    AndroidView(
        { mapViewState },
        Modifier
    ) { mapView -> onLoad?.invoke(mapView) }

    // TODO
    // I'm making a new ThrowViewModel object here that should be made somewhere else and injected
    val throwViewModel = remember{
        ThrowViewModel(
            selectedLocation,
            mapViewState,
            getWeather = getWeather,
            planeRepository = planeRepository,
            weatherRepository = weatherRepository
        )
    }

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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.plane01),
            contentDescription = "TODO",
            modifier = Modifier
                .rotate((throwViewModel.planeState.collectAsState().value.angle).toFloat())
                .scale(throwViewModel.getPlaneScale())
        )
    }


    Column {
        Text(text = "Wind angle: ${throwViewModel.getWindAngle().toFloat()} - speed: ${"%.2f".format(throwViewModel.getWindSpeed().toFloat())}")
        Text(text = "Plane angle: ${throwViewModel.planeState.collectAsState().value.angle.toFloat()} - speed: ${"%.2f".format(throwViewModel.planeState.collectAsState().value.speed.toFloat())}")
        Text(text = "Plane pos: \n${throwViewModel.planeState.collectAsState().value.pos[0].toFloat()}\n${throwViewModel.planeState.collectAsState().value.pos[1].toFloat()}")


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
                val plane = throwViewModel.planeState.value
                throwViewModel.planeRepository.update(plane.copy(angle = value.toDouble() * 360))
                sliderPosition = value
                throwViewModel.previousPlanePos = throwViewModel.startPos
            },
            enabled = !throwViewModel.planeState.collectAsState().value.flying,
        )
    }
}
