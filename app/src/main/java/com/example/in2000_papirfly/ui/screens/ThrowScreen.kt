package com.example.in2000_papirfly.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_papirfly.R
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.composables.PlaneComposable
import com.example.in2000_papirfly.ui.viewmodels.ThrowScreenState
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModel
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import org.osmdroid.util.GeoPoint
import io.ktor.util.reflect.*
import java.lang.Math.*
import kotlin.math.atan2
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView

@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThrowScreen(
    modifier: Modifier = Modifier,
    selectedLocation : GeoPoint,
    locationName: String,
    onLoad: ((map: MapView) -> Unit)? = null,
    onBack: () -> Unit
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val mapViewState = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val rowState = rememberLazyListState()

    val animateRow = { position: Int ->
        scope.launch {
            rowState.animateScrollToItem(position)
        }
    }

    // merge stuff
    val appContainer = (LocalContext.current.applicationContext as PapirflyApplication).appContainer
    val throwViewModel = remember {
        appContainer.throwViewModelFactory.newViewModel(
            locationName = locationName,
            selectedLocation = selectedLocation,
            mapViewState = mapViewState,
            openBottomSheet = { position: Int ->
                openBottomSheet = true
                animateRow(position)
            }
        )
    }

    val throwPointWeather = throwViewModel.throwWeatherState.collectAsState().value.weather
    val highScores = throwViewModel.throwPointHighScores.collectAsState()
    val highScoreOnMap = throwViewModel.highScoresOnMapState.collectAsState()
    val throwScreenState = throwViewModel.getThrowScreenState()

    BackHandler {
        Log.d("ThrowScreen", "Back press detected")
        throwViewModel.planeFlying.cancel()
        throwViewModel.resetPlane()
        onBack()
    }

    // Map composable
    AndroidView(
        { mapViewState },
        Modifier.border(1.dp, Color(0xFF000000))
    ) { mapView -> onLoad?.invoke(mapView) }

    // This fixes the map glitching
    mapViewState.controller.setCenter(throwViewModel.previousPlanePos)
    mapViewState.controller.setZoom(12.0)

    // Paper plane
    PlaneComposable(
        planeSize = throwViewModel.getPlaneScale(),
        planeState = throwViewModel.planeState,
        planeVisible = showPlane(throwScreenState)
    )

    Column() {
        Text(
            text = "LOCAL HIGHSCORE: ${highScores.value[locationName]!!.distance}km",
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

//    Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth(),
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            LazyRow(state = rowState) {
                items(throwPointWeather.size) {
                    val location = throwPointWeather[it]
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .size(width = 380.dp, height = 200.dp),
                        onClick = {
                            if (throwPointWeather[it].namePos == throwViewModel.locationName) {
                                openBottomSheet = false
                            } else {
                                scope.launch {
                                    rowState.animateScrollToItem(it)
                                }
                                val newLocation =
                                    ThrowPointList.throwPoints[throwPointWeather[it].namePos]
                                throwViewModel.previousPlanePos = mapViewState.mapCenter as GeoPoint
                                mapViewState.controller.animateTo(newLocation, 12.0, 1000)
                                throwViewModel.moveLocation(
                                    newLocation!!,
                                    throwPointWeather[it].namePos!!
                                )
                            }
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            text = "${throwPointWeather[it].namePos}",
                            fontSize = 30.sp
                        )

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val context = LocalContext.current.applicationContext
                            val resources = context.resources
                            val packageName = context.packageName
                            val id = resources.getIdentifier(location.icon, "drawable", packageName)

                            Icon(
                                painter = painterResource(id = id),
                                contentDescription = "Weather Icon",
                                modifier = modifier.size(size = 65.dp),
                                tint = Color.Unspecified
                            )

                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                text = "${"%.0f".format(location.temperature)}°C",
                                fontSize = 28.sp
                            )

                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                text = "${"%.0f".format(location.rain)}mm",
                                fontSize = 18.sp
                            )

                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                text = "${"%.0f".format(location.windSpeed)}m/s",
                                fontSize = 18.sp
                            )

                            Icon(
                                painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                                modifier = modifier
                                    .size(size = 45.dp)
                                    .rotate(location.windAngle.toFloat() + 90.toFloat()),
                                contentDescription = "Vindretning",
                            )
                        }
                        // High score banner
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(end = 10.dp),
                                text = "Highscore: ${highScores.value[location.namePos]!!.distance}km",
                                fontSize = 16.sp
                            )

                            Button (
//                                modifier = Modifier.shadow(
//                                    elevation = 10.dp,
//                                    ambientColor = Color.Black,
//                                    spotColor = Color.Black
//                                ),
                                modifier = Modifier
                                    .size(180.dp, 45.dp),
                                enabled = highScores.value[location.namePos]!!.distance != 0,
                                onClick = {
                                    if (!highScoreOnMap.value[location.namePos]!!) {
                                        drawHighScorePath(mapViewState.overlays, highScores.value[location.namePos]!!.flightPath!!, location.namePos!!)
                                        drawGoalMarker(
                                            { HighScoreMarker(mapViewState, location.namePos!!) },
                                            mapViewState.overlays,
                                            highScores.value[location.namePos]!!.flightPath!![0],
                                            highScores.value[location.namePos]!!.flightPath!!.last(),
                                            true
                                        )
                                    } else {
                                        removeHighScorePath(mapViewState.overlays, location.namePos!!)
                                        mapViewState.invalidate()
                                    }
                                    throwViewModel.updateHighScoreShownState(location.namePos!!)
                                },
                                colors = ButtonDefaults.buttonColors(com.example.in2000_papirfly.ui.theme.colOrange),
                                shape = RoundedCornerShape(10),
                            ) {
                                Text(
                                    text = if (!highScoreOnMap.value[location.namePos]!!) "Vis highscore" else "Skjul highscore",
                                    fontSize = 16.sp,
                                )
                            }
                        }
                    }
                }
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
