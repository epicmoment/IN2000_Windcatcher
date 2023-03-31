package com.example.in2000_papirfly.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.R
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModel
import org.osmdroid.util.GeoPoint
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.rememberMapViewWithLifecycle
import org.osmdroid.views.MapView

@Composable
fun ThrowScreen(
    selectedLocation : GeoPoint,
    onLoad: ((map: MapView) -> Unit)? = null
) {

    // fun Map View() {
    val mapViewState = rememberMapViewWithLifecycle()
    AndroidView(
        { mapViewState },
        Modifier
    ) { mapView -> onLoad?.invoke(mapView) }

    // TODO
    // I'm making a new ThrowViewModel object here that should be made somewhere else and injected
    val throwViewModel = remember{ ThrowViewModel(selectedLocation, mapViewState) }

    // This fixes the map glitching
    mapViewState.controller.setCenter(throwViewModel.previousPlanePos)

    // Wind arrow
    Image(
        painter = painterResource(id = R.drawable.arrow01),
        contentDescription = "TODO",
        modifier = Modifier
            .rotate((throwViewModel.getWindAngle() - 90).toFloat())
            .scale(0.2f)
    )


    Column(){
        Text(text = "Throw Screen wow!")
        Text(text = "Wind angle: ${throwViewModel.getWindAngle().toFloat()} - speed: ${"%.2f".format(throwViewModel.getWindSpeed().toFloat())}")
        Text(text = "Plane angle: ${throwViewModel.getPlaneAngle().toFloat()} - speed: ${"%.2f".format(throwViewModel.getPlaneSpeed().toFloat())}")
        Text(text = "Plane pos: \n${throwViewModel.getPlanePos()[0].toFloat()}\n${throwViewModel.getPlanePos()[1].toFloat()}")

        // Paper plane
        Image(
            painter = painterResource(id = R.drawable.plane01),
            contentDescription = "TODO",
            modifier = Modifier
                .rotate((throwViewModel.getPlaneAngle() - 90.0).toFloat())
                .scale(throwViewModel.getPlaneScale())
        )


        Text(
            text = "Height: ${"%.0f".format(throwViewModel.getPlaneHeight())}")


        Button(
            onClick = {
                throwViewModel.throwPlane();
            }
        ){
            Text("Throw")
        }

    }
}