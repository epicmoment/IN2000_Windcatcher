package com.example.in2000_papirfly.ui.screens

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.helpers.WeatherConstants.AIR_PRESSURE_NORMAL
import com.example.in2000_papirfly.ui.composables.FlightLog
import com.example.in2000_papirfly.ui.composables.PlaneComposable
import com.example.in2000_papirfly.ui.theme.colBlueTransparent
import com.example.in2000_papirfly.ui.theme.colRed
import com.example.in2000_papirfly.ui.viewmodels.ThrowScreenState
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModel
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawGoalMarker
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawHighScorePath
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.getRotationAngle
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.removeHighScorePath
import io.ktor.util.reflect.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.lang.Math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThrowScreen(
    selectedLocation : GeoPoint,
    locationName: String,
    changeLocation: (locationPoint: GeoPoint, locationName: String) -> Unit,
    onCustomizePage: () -> Unit,
    onLoad: ((map: MapView) -> Unit)? = null,
    onBack: () -> Unit
) {

    // Initializes dependencies for the view model
    val context = LocalContext.current.applicationContext
    val mapViewState = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    val rowState = rememberLazyListState()
    val appContainer = (context as PapirflyApplication).appContainer
    val animateRow = { position: Int ->
        scope.launch {
            rowState.animateScrollToItem(position)
        }
    }

    val logBottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = logBottomSheetState
    )

    // Initializes the view model
    val throwViewModel = remember {
        appContainer.throwViewModelFactory.newViewModel(
            locationName = locationName,
            selectedLocation = selectedLocation,
            mapViewState = mapViewState,
            openBottomSheet = { position: Int ->
                animateRow(position)
            },
            changeLocation = changeLocation,
            onShowLog = {
                scope.launch {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        )
    }

    // Fetches observable state of the screen
    val throwScreenState = throwViewModel.getThrowScreenState().collectAsState().value

    // Defines the behaviour of the "back"-button to prevent a crash
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

    // Shows the map
    AndroidView(
        { mapViewState },
        Modifier.border(1.dp, Color(0xFF000000))
    ) {
        mapView -> onLoad?.invoke(mapView)
    }

    // Paper plane
    PlaneComposable(
        planeSize = throwViewModel.getPlaneScale(),
        planeState = throwViewModel.planeState,
        planeVisible = showPlane(throwScreenState)
    )

    // Flight info box
    if (throwScreenState != ThrowScreenState.ViewingLog) {
        FlightInfoBox(
            context = context,
            throwViewModel = throwViewModel,
        )
    }

    // Circular Slider - only shown when screen state is set to "Throwing"
    if (throwScreenState == ThrowScreenState.Throwing) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularSlider(
                throwViewModel,
                mapViewState,
            )
        }
    }

    // Button panel for throwing, navigating and customizing
    if (throwScreenState == ThrowScreenState.MovingMap || throwScreenState == ThrowScreenState.Throwing) {
        ButtonPanel(
            throwScreenState = throwScreenState,
            throwViewModel = throwViewModel,
            scope = scope,
            rowState = rowState,
            onCustomizePage = onCustomizePage,
        )
    }

    // Navigation and high score drawer
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    if (throwScreenState == ThrowScreenState.ChoosingPosition) {
        PositionAndHighScoreDrawer(
            context = context,
            throwViewModel = throwViewModel,
            bottomSheetState = bottomSheetState,
            rowState = rowState,
            scope = scope,
            mapViewState = mapViewState,
            changeLocation = changeLocation,
        )
    }

    FlightLog(
        logStateParam = throwViewModel.logState,
        scaffoldState = scaffoldState,
        centerMap = { pos ->
            mapViewState.controller.animateTo(pos)
        }
    ) {
        throwViewModel.closeLog()
    }

}

@Composable
fun showPlane(throwScreenState: ThrowScreenState): Boolean{
    val value = when (throwScreenState){
        is ThrowScreenState.Throwing -> true
        is ThrowScreenState.Flying -> true
        is ThrowScreenState.MovingMap -> false
        is ThrowScreenState.ChoosingPosition -> false
        is ThrowScreenState.ViewingLog -> false
    }
    return value
}

//Code tatt fra stackoverflow
//Rafsanjani answered Dec 20, 2021 at 16:02
@Composable
fun CircularSlider(
    throwViewModel: ThrowViewModel,
    mapViewState: DisableMapView,
) {
    var radius by remember { mutableStateOf(0f) }
    var shapeCenter by remember { mutableStateOf(Offset.Zero) }
    var handleCenter by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(270.0) }

    val openMarker = {
        throwViewModel.setThrowScreenState(ThrowScreenState.MovingMap)
        mapViewState.overlays.forEach {
            if (it is ThrowPositionMarker && it.title == throwViewModel.locationName) {
                it.onMarkerClickDefault(it, mapViewState)
                return@forEach
            }
        }
    }

    // The outer bounding box containing the slider
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(180.dp * 2f)
    ) {
        // The canvas on which the slider is drawn
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
            drawCircle(color = colRed, center = handleCenter, radius = 50f)
        }
        // Box for passing clicks in the center of the slider through to the map
        Box(modifier = Modifier
            .size(50.dp * 2f)
            .clickable(
                true,
                onClick = {
                    Log.d("OpenMarker", "Click detected.")
                    openMarker()
                }
            ),
            contentAlignment = Alignment.Center) {
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun FlightInfoBox(
    modifier: Modifier = Modifier,
    context: Context,
    throwViewModel: ThrowViewModel,
) {
    val resources = context.resources
    val packageName = context.packageName
    val id = resources.getIdentifier(throwViewModel.weather.icon, "drawable", packageName)
    val planeState = throwViewModel.planeState.collectAsState().value

    var airPressureDescription = stringResource(R.string.low_air_pressure_display)
    var airPressureColor = Color.Red

    if (throwViewModel.weather.airPressure > 1013) {
        airPressureDescription = stringResource(R.string.high_air_pressure_display)
        airPressureColor = Color.Blue
    }

    // Invisible box filling the entire screen
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Box containing the info elements
        Box(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .clip(RoundedCornerShape(14.dp))
                .background(colBlueTransparent),
        ) {
            // Column for positioning the info elements correctly
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
            ) {
                // Box containing weather icon, wind arrow and air pressure symbol
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Weather icon
                        Icon(
                            painter = painterResource(id = id),
                            contentDescription = stringResource(R.string.weather_icon_description),
                            modifier = modifier
                                .padding(start = 20.dp, top = 20.dp)
                                .size(size = 80.dp),
                            tint = Color.Unspecified
                        )

                        // Wind direction indicator
                        Image(
                            modifier = Modifier
                                .padding(end = 40.dp, top = 10.dp)
                                .rotate((throwViewModel.weather.windAngle + 180).toFloat())
                                .size(100.dp),
                            painter = painterResource(id = R.drawable.up_arrow__1_),
                            contentDescription = stringResource(R.string.wind_direction_arrow_description),
                            colorFilter = ColorFilter.tint(Color.White)
                        )

                        // Air pressure status
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
                // Row showing speed and height data
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Speed indicator
                    Text(
                        modifier = Modifier
                            .padding(start = 40.dp),
                        text = stringResource(R.string.speed_display, "%.2f".format(planeState.speed.toFloat())),
                        fontSize = 20.sp,
                        color = Color.White,
                    )

                    // Height indicator
                    Text(
                        modifier = Modifier
                            .padding(end = 40.dp),
                        text = stringResource(R.string.height_display, if (planeState.height >= 0) "%.0f".format(planeState.height) else 0),
                        fontSize = 20.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
fun ButtonPanel(
    throwScreenState: ThrowScreenState,
    throwViewModel: ThrowViewModel,
    scope: CoroutineScope,
    rowState: LazyListState,
    onCustomizePage: () -> Unit,
) {
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
                colors = ButtonDefaults.buttonColors(colRed),
                shape = RoundedCornerShape(20),
            ) {
                Text(
                    text = if (throwScreenState == ThrowScreenState.Throwing) stringResource(R.string.throw_string).uppercase() else stringResource(R.string.ready).uppercase(),
                    fontSize = 35.sp,
                    color = Color.White
                )
            }

        Spacer(
            Modifier
                .height(50.dp)
        )

            Row {
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
                    colors = ButtonDefaults.buttonColors(colRed),
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
                        contentDescription = stringResource(R.string.position_selection_card_description),
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
                    colors = ButtonDefaults.buttonColors(colRed),
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
                        contentDescription = stringResource(R.string.customize_page_description),
                        modifier = Modifier.size(size = 30.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }

@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionAndHighScoreDrawer(
    modifier: Modifier = Modifier,
    context: Context,
    throwViewModel: ThrowViewModel,
    bottomSheetState: SheetState,
    rowState: LazyListState,
    scope: CoroutineScope,
    mapViewState: DisableMapView,
    changeLocation: (locationPoint: GeoPoint, locationName: String) -> Unit,
) {
    val resources = context.resources
    val packageName = context.packageName
    val throwPointWeather = throwViewModel.throwWeatherState.collectAsState().value.weather
    val highScores = throwViewModel.throwPointHighScores.collectAsState().value
    val highScoreOnMap = throwViewModel.highScoresOnMapState.collectAsState().value

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
                    text = stringResource(R.string.choose_throw_location),
                    fontSize = 20.sp,
                )
            }
        }
    ) {
        // Cards for the different throw locations
        LazyRow(state = rowState) {
            items(throwPointWeather.size) {

                val location = throwPointWeather[it]
                val id = resources.getIdentifier(location.icon, "drawable", packageName)

                // Clickable card
                Card(
                    shape = MaterialTheme.shapes.medium,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .size(width = 380.dp, height = 200.dp),
                    onClick = {
                        if (location.namePos == throwViewModel.locationName) {
                            // Sets ThrowScreenState to Throwing when sheet is dismissed
                            throwViewModel.changeAngle(0.toFloat())
                        } else {
                            scope.launch {
                                rowState.animateScrollToItem(it)
                            }
                            val newLocation = ThrowPointList.throwPoints.getOrDefault(
                                location.namePos,
                                GeoPoint(0.0, 0.0)
                            )

                            throwViewModel.previousPlanePos = mapViewState.mapCenter as GeoPoint
                            mapViewState.controller.animateTo(newLocation, 12.0, 1000)
                            throwViewModel.moveLocation(
                                newLocation,
                                location.namePos
                            )
                            // Sets the Screen State location to the new location
                            changeLocation(newLocation, location.namePos)
                        }
                    }
                ) {
                    // Place name and weather icon
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Place name
                        Text(
                            modifier = modifier.padding(start = 12.dp, top = 6.dp),
                            text = location.namePos,
                            fontSize = 30.sp
                        )

                        // Weather icon
                        Icon(
                            painter = painterResource(id = id),
                            contentDescription = stringResource(R.string.weather_icon_description),
                            modifier = modifier
                                .padding(end = 20.dp, top = 5.dp)
                                .size(size = 65.dp),
                            tint = Color.Unspecified
                        )
                    }

                    // Row containing temperature, precipitation, wind direction and air pressure
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Temperature
                        Text(
                            modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            text = stringResource(R.string.temperature_display, "%.0f".format(location.temperature)),
                            fontSize = 28.sp
                        )

                        // Precipitation
                        Text(
                            modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            text = stringResource(R.string.rain_display, "%.0f".format(location.rain)),
                            fontSize = 18.sp
                        )

                        // Wind speed
                        Text(
                            modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            text = stringResource(R.string.wind_speed_display, "%.0f".format(location.windSpeed)),
                            fontSize = 18.sp
                        )

                        // Wind direction arrow
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                            modifier = modifier
                                .size(size = 45.dp)
                                .rotate(location.windAngle.toFloat() + 90.toFloat()),
                            contentDescription = stringResource(R.string.wind_direction_arrow_description, location.windAngle.toInt()),
                        )

                        // Air pressure symbol
                        var airPressureDescription = stringResource(R.string.low_air_pressure_display)
                        var airPressureColor = Color.Red
                        if (location.airPressure > AIR_PRESSURE_NORMAL) {
                            airPressureDescription = stringResource(R.string.high_air_pressure_display)
                            airPressureColor = Color.Blue
                        }

                        Text(
                            modifier = modifier.padding(
                                start = 10.dp,
                                end = 10.dp,
                                top = 8.dp,
                                bottom = 8.dp
                            ),
                            text = airPressureDescription,
                            fontSize = 28.sp,
                            color = airPressureColor
                        )
                    }

                    // High score banner
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val locationName = highScores.getOrDefault(
                            location.namePos,
                            HighScore()
                        )
                        val highScoreShown = highScoreOnMap.getOrDefault(
                            location.namePos,
                            false
                        )

                        // High score distance
                        Text(
                            modifier = modifier
                                .padding(end = 10.dp),
                            text = stringResource(R.string.highscore_display, locationName.distance),
                            fontSize = 16.sp
                        )

                        // Button for toggling if high score path is shown or not
                        Button (
                            modifier = modifier
                                .size(180.dp, 45.dp),
                            enabled = locationName.distance != 0,
                            onClick = {
                                if (!highScoreShown) {
                                    drawHighScorePath(mapViewState.overlays, locationName.flightPath, location.namePos)
                                    drawGoalMarker(
                                        { HighScoreMarker(mapViewState, location.namePos) },
                                        mapViewState.overlays,
                                        locationName.flightPath[0],
                                        locationName.flightPath.last(),
                                        true
                                    )
                                } else {
                                    removeHighScorePath(mapViewState.overlays, location.namePos)
                                    mapViewState.invalidate()
                                }
                                throwViewModel.updateHighScoreShownState(location.namePos)
                            },
                            colors = ButtonDefaults.buttonColors(colRed),
                            shape = RoundedCornerShape(10),
                        ) {
                            Text(
                                text = if (!highScoreShown) stringResource(R.string.show_highscore) else stringResource(R.string.hide_highscore),
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}