package com.example.in2000_papirfly.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.composables.PlaneComposable
import com.example.in2000_papirfly.ui.viewmodels.ThrowScreenState
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModel
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.lang.Math.*
import kotlin.math.atan2

@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThrowScreen(
    modifier: Modifier = Modifier,
    selectedLocation : GeoPoint,
    locationName: String,
    changeLocation: (locationPoint: GeoPoint, locationName: String) -> Unit,
    onCustomizePage: () -> Unit,
    onLoad: ((map: MapView) -> Unit)? = null,
    onBack: () -> Unit
) {
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

    val appContainer = (LocalContext.current.applicationContext as PapirflyApplication).appContainer
    val throwViewModel = remember {
        appContainer.throwViewModelFactory.newViewModel(
            locationName = locationName,
            selectedLocation = selectedLocation,
            mapViewState = mapViewState,
            openBottomSheet = { position: Int ->
                animateRow(position)
            },
            changeLocation = changeLocation
        )
    }

    val toggleMarkerInfoWindow = {
        throwViewModel.setThrowScreenState(ThrowScreenState.MovingMap)
        mapViewState.overlays.forEach {
            if (it is ThrowPositionMarker && it.title == throwViewModel.locationName) {
                it.onMarkerClickDefault(it, mapViewState)
                return@forEach
            }
        }
    }

    val throwPointWeather = throwViewModel.throwWeatherState.collectAsState().value.weather
    val highScores = throwViewModel.throwPointHighScores.collectAsState()
    val highScoreOnMap = throwViewModel.highScoresOnMapState.collectAsState()
    val throwScreenState = throwViewModel.getThrowScreenState().collectAsState().value
    val planeState = throwViewModel.planeState.collectAsState().value

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

    // Top info panel. Might not be needed
    // TODO make text visible in dark mode
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Plane speed: ${"%.2f".format(planeState.speed.toFloat())}",
            fontSize = 15.sp,
            color = com.example.in2000_papirfly.ui.theme.md_theme_dark_onPrimary,
        )

        Text(
            text = "Height: ${"%.0f".format(planeState.height)}",
            fontSize = 15.sp,
            color = com.example.in2000_papirfly.ui.theme.md_theme_dark_onPrimary,
        )

        // Wind arrow
        Image(
            painter = painterResource(id = R.drawable.up_arrow__1_),
            contentDescription = "TODO",
            modifier = Modifier
                .padding(top = 20.dp)
                .rotate((throwViewModel.weather.windAngle + 180).toFloat())
                .size(80.dp)
        )
    }

    // Column containing wind arrow, circular slider and throw button
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
    ) {

        Spacer(modifier = Modifier.height(180.dp))

        // Circular Slider
        if (throwScreenState == ThrowScreenState.Throwing) {
            CircularSlider(
                throwViewModel,
                toggleMarkerInfoWindow,
            )
        } else {
            Spacer(modifier = Modifier.height(360.dp))
        }

        // Buttons - only shown if user is moving on the map or about to throw
        if (throwScreenState == ThrowScreenState.MovingMap || throwScreenState == ThrowScreenState.Throwing) {
            Button(
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    ambientColor = Color.Black,
                    spotColor = Color.Black,
                ),
                onClick = {
                    // Throws the plane
                    if (throwScreenState == ThrowScreenState.Throwing) throwViewModel.throwPlane()
                    // Sets state to "Throwing"
                    else throwViewModel.changeAngle(0.toFloat())
                },
                colors = ButtonDefaults.buttonColors(com.example.in2000_papirfly.ui.theme.colOrange),
                shape = RoundedCornerShape(10),
            ) {
                Text(
                    text = if (throwScreenState == ThrowScreenState.Throwing) "KAST" else "KLAR",
                    fontSize = 35.sp,
                    color = Color.White
                )
            }

            Row() {
                // Opens position drawer
                Button(
                    onClick = {
                        scope.launch {
                            rowState.animateScrollToItem(
                                ThrowPointList.throwPoints.keys.indexOf(
                                    throwViewModel.locationName
                                )
                            )
                        }
                        throwViewModel.setThrowScreenState(ThrowScreenState.ChoosingPosition)
                    },
                    colors = ButtonDefaults.buttonColors(com.example.in2000_papirfly.ui.theme.colOrange),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        )
                        .shadow(
                            elevation = 0.dp,
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        )
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.partlycloudy_day
                        ),
                        contentDescription = "See current weather and choose position",
                        modifier = Modifier
                            .size(30.dp),
                        tint = Color.White
                    )
                }
                // Goes to customization screen
                Button (
                    onClick = onCustomizePage,
                    colors = ButtonDefaults.buttonColors(com.example.in2000_papirfly.ui.theme.colOrange),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        )
                        .shadow(
                            elevation = 0.dp,
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.construction),
                        contentDescription = "Customize Page",
                        modifier = Modifier.size(size = 30.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Navigation and high score sheet
    if (throwScreenState == ThrowScreenState.ChoosingPosition) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth(),
            onDismissRequest = {
                // Sets ThrowScreenState to Throwing when sheet is dismissed
                throwViewModel.changeAngle(0.toFloat())
            },
            sheetState = bottomSheetState,
            dragHandle = {
                Card(
                    modifier = Modifier
                        .padding(8.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp),
                        text = "Velg kastested",
                        fontSize = 20.sp,
                    )
                }
            }
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
                                // Sets ThrowScreenState to Throwing when sheet is dismissed
                                throwViewModel.changeAngle(0.toFloat())
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
                                // Sets the Screen State location to the new location
                                changeLocation(newLocation, throwPointWeather[it].namePos!!)
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
fun showPlane(throwScreenState: ThrowScreenState): Boolean{
    val value = when (throwScreenState){
        is ThrowScreenState.Throwing -> true
        is ThrowScreenState.Flying -> true
        is ThrowScreenState.MovingMap -> false
        is ThrowScreenState.ChoosingPosition -> false
    }
    return value
}

//Code tatt fra stackoverflow
//Rafsanjani answered Dec 20, 2021 at 16:02
@Composable
fun CircularSlider(
    throwViewModel: ThrowViewModel,
    openMarker: () -> Unit
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
                .size(170.dp * 2f)
                .padding(20.dp)
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
                        onPress = { offset ->
                            handleCenter = Offset.Zero + offset
                            angle = getRotationAngle(handleCenter, shapeCenter)

                            val angleForPlane = angle.toFloat() + 90

                            throwViewModel.changeAngle(angleForPlane)
                        }
                    )
                }
        ) {
            shapeCenter = center

            radius = (size.minDimension / 2) - 50

            val x = (shapeCenter.x + cos(toRadians(angle)) * radius).toFloat()
            val y = (shapeCenter.y + sin(toRadians(angle)) * radius).toFloat()

            handleCenter = Offset(x, y)

            drawCircle(color = Color.Black.copy(alpha = 0.10f), style = Stroke(20f), radius = radius)
            drawCircle(color = com.example.in2000_papirfly.ui.theme.colOrange, center = handleCenter, radius = 50f)
        }

        Box(modifier = Modifier
            .size(50.dp * 2f)
            .clickable(true, onClick = {
                Log.d("OpenMarker", "Click detected.")
                openMarker()
            }
            ),
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
