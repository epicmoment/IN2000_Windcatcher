package com.example.in2000_papirfly.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowVievModelUtilities.drawGoalMarker
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowVievModelUtilities.drawHighScorePath
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowVievModelUtilities.removeHighScorePath
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

    val context = LocalContext.current.applicationContext
    val resources = context.resources
    val packageName = context.packageName

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

    // This prevents the app from crashing when switching to dark mode
    DisposableEffect(throwViewModel) {
        onDispose {
            throwViewModel.planeFlying.cancel()
            throwViewModel.resetPlane()
        }
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

    // Flight info box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        val id = resources.getIdentifier(throwViewModel.weather.icon, "drawable", packageName)
        Box(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0, 0, 0, 100)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Icon(
                            painter = painterResource(id = id),
                            contentDescription = "Weather Icon",
                            modifier = modifier
                                .padding(start = 12.dp)
                                .size(size = 80.dp),
                            tint = Color.Unspecified
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.up_arrow__1_),
                            contentDescription = "Weather direction arrow",
                            modifier = Modifier
                                .rotate((throwViewModel.weather.windAngle + 180).toFloat())
                                .size(80.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        var airPressureDescription = "L"
                        var airPressureColor = Color.Red

                        if (throwViewModel.weather.airPressure > 1013) {
                            airPressureDescription = "H"
                            airPressureColor = Color.Blue
                        }

                        Text(
                            modifier = Modifier
                                .padding(end = 40.dp),
                            text = airPressureDescription,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = airPressureColor
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 40.dp),
                        text = "Fart: ${"%.2f".format(planeState.speed.toFloat())}",
                        fontSize = 20.sp,
                        color = Color.White,
                    )

                    Text(
                        modifier = Modifier
                            .padding(end = 40.dp),
                        text = "Høyde: ${if (planeState.height >= 0) "%.0f".format(planeState.height) else 0}",
                        fontSize = 20.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }

    // Circular Slider
    if (throwScreenState == ThrowScreenState.Throwing) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularSlider(
                throwViewModel,
                toggleMarkerInfoWindow,
            )
        }
    }

    /* Column containing throw, position and customization buttons
     * Only shown if user is moving map or throwing
     */
    if (throwScreenState == ThrowScreenState.MovingMap || throwScreenState == ThrowScreenState.Throwing) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {

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

            Spacer(
                Modifier
                    .height(50.dp)
            )

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
                    onClick = {
                        throwViewModel.planeFlying.cancel()
                        throwViewModel.resetPlane()
                        onCustomizePage()
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
            // Cards for the different throw locations
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
                        val id = resources.getIdentifier(location.icon, "drawable", packageName)

                        // Place name and weather icon
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    text = "${throwPointWeather[it].namePos}",
                                    fontSize = 30.sp
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(
                                    painter = painterResource(id = id),
                                    contentDescription = "Weather Icon",
                                    modifier = modifier.size(size = 65.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

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

                            var airPressureDescription = "L"
                            var airPressureColor = Color.Red

                            if (location.airPressure > 1013) {
                                airPressureDescription = "H"
                                airPressureColor = Color.Blue
                            }

                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                text = "hPa:",
                                fontSize = 18.sp,
                            )

                            Text(
                                modifier = Modifier.padding(end = 10.dp, top = 8.dp, bottom = 8.dp),
                                text = airPressureDescription,
                                fontSize = 28.sp,
                                color = airPressureColor
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

            val x = (shapeCenter.x + kotlin.math.cos(toRadians(angle)) * radius).toFloat()
            val y = (shapeCenter.y + kotlin.math.sin(toRadians(angle)) * radius).toFloat()

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
