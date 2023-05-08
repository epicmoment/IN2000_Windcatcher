package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.lang.Math.*
import kotlin.math.atan2

@Composable
fun ThrowScreen(
    selectedLocation : GeoPoint,
    locationName: String,
    onLoad: ((map: MapView) -> Unit)? = null,
    getWeather: (location: String) -> Weather,
    weatherRepository: DataRepository,
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
            locationName,
            selectedLocation,
            mapViewState,
            getWeather = getWeather,
            planeRepository = planeRepository,
            weatherRepository = weatherRepository
        )
    }
    val highscore = throwViewModel.highScore.collectAsState()

    val throwScreenState = throwViewModel.getThrowScreenState()

    // This fixes the map glitching
    mapViewState.controller.setCenter(throwViewModel.previousPlanePos)

    // Paper plane
    PlaneComposable(
        planeSize = throwViewModel.getPlaneScale(),
        planeState = throwViewModel.planeState,
        planeVisible = showPlane(throwScreenState)
    )

    Column() {
        Text(
            text = "LOCAL HIGHSCORE: ${highscore.value.distance}km",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Plane speed: ${"%.2f".format(throwViewModel.planeState.collectAsState().value.speed.toFloat())}",
            fontSize = 15.sp
        )

        Text(
            text = "Height: ${"%.0f".format(throwViewModel.planeState.collectAsState().value.height)}",
            fontSize = 15.sp
        )
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
    ) {
        // Wind arrow
        Image(
            painter = painterResource(id = R.drawable.up_arrow__1_),
            contentDescription = "TODO",
            modifier = Modifier
                .rotate((throwViewModel.weather.windAngle + 180).toFloat())
                .size(80.dp)
        )

        // Circular Slider
        if (throwViewModel.planeState.collectAsState().value.height > 99.9) {
            CircularSlider(
                throwViewModel,
            )
        }
        if (throwViewModel.planeState.collectAsState().value.height < 99.9){
            Spacer(modifier = Modifier.height(250.dp))
        }

        if(throwViewModel.planeState.collectAsState().value.height > 99.9) {
            Button(
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                ),
                //enabled = !throwViewModel.planeState.collectAsState().value.flying,
                onClick = {
                    throwViewModel.throwPlane()
                },
                colors = ButtonDefaults.buttonColors(com.example.in2000_papirfly.ui.theme.colOrange),
                shape = RoundedCornerShape(10),
            ){
                    Text(
                        text = "KAST",
                        fontSize = 35.sp,)
                }
            }
            if (throwViewModel.planeState.collectAsState().value.height == 0.toDouble()){
                Button(
                    modifier = Modifier.shadow(
                        elevation = 10.dp,
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    ),
                    //enabled = !throwViewModel.planeState.collectAsState().value.flying,
                    onClick = {
                        throwViewModel.changeAngle(0.toFloat())
                        //throwViewModel.setPlaneStart()
                    },
                    colors = ButtonDefaults.buttonColors(com.example.in2000_papirfly.ui.theme.colOrange),
                    shape = RoundedCornerShape(10),
                ) {
                    Text(
                        text = "KAST IGJEN",
                        fontSize = 35.sp,
                    )
                }
            }
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

//Code tatt fra stackoverflow
//Rafsanjani answered Dec 20, 2021 at 16:02
@Composable
fun CircularSlider(
    throwViewModel: ThrowViewModel,
) {
    var radius by remember {
        mutableStateOf(0f)
    }

    var shapeCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var handleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var angle by remember {
        mutableStateOf(270.0)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(180.dp * 2f)
    ) {
        Canvas(
            modifier = Modifier
                .size(180.dp * 2f)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        handleCenter += dragAmount
                        angle = getRotationAngle(handleCenter, shapeCenter)

                        val angleForPlane = angle.toFloat() + 90

                        throwViewModel.changeAngle(angleForPlane)
                        change.consume()
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            handleCenter = Offset.Zero + offset
                            angle = getRotationAngle(handleCenter, shapeCenter)

                            val angleForPlane = angle.toFloat() + 90

                            throwViewModel.changeAngle(angleForPlane)
                        },
                    )
                }
                .padding(50.dp)
        ) {
            shapeCenter = center

            radius = size.minDimension / 2

            val x = (shapeCenter.x + cos(toRadians(angle)) * radius).toFloat()
            val y = (shapeCenter.y + sin(toRadians(angle)) * radius).toFloat()

            handleCenter = Offset(x, y)

            drawCircle(color = Color.Black.copy(alpha = 0.10f), style = Stroke(20f), radius = radius)
            drawCircle(color = com.example.in2000_papirfly.ui.theme.colOrange, center = handleCenter, radius = 50f)
        }

        Box(modifier = Modifier
            .size(120.dp * 2f)
            .clickable(false, onClick = {}),
            contentAlignment = Alignment.Center) {
        }
    }
}

private fun getRotationAngle(currentPosition: Offset, center: Offset): Double {
    val (dx, dy) = currentPosition - center
    val theta = atan2(dy, dx).toDouble()

    var angle = toDegrees(theta)

    if (angle < 0) {
        angle += 360.0
    }
    return angle
}
