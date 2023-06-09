package no.met.in2000.windcatcher.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import no.met.in2000.windcatcher.WindcatcherApplication
import no.met.in2000.windcatcher.R
import no.met.in2000.windcatcher.data.*
import no.met.in2000.windcatcher.data.components.HighScore
import no.met.in2000.windcatcher.data.components.ThrowPointList
import no.met.in2000.windcatcher.data.screenuistates.ThrowScreenState
import no.met.in2000.windcatcher.data.screenuistates.ThrowScreenUIState
import no.met.in2000.windcatcher.helpers.WeatherConstants.AIR_PRESSURE_NORMAL
import no.met.in2000.windcatcher.ui.composables.FlightLog
import no.met.in2000.windcatcher.ui.composables.PlaneComposable
import no.met.in2000.windcatcher.ui.theme.*
import no.met.in2000.windcatcher.ui.viewmodels.ThrowViewModel
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.*
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawGoalMarker
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawHighScorePath
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.getRotationAngle
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.removeHighScorePath
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
    val appContainer = (context as WindcatcherApplication).appContainer
    val animateRow = { position: Int ->
        scope.launch {
            rowState.animateScrollToItem(position)
        }
    }

    val logBottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
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
        )
    }

    // Fetches observable state of the screen
    val throwScreenState = throwViewModel.uiState.collectAsState().value

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
        mapView ->
            onLoad?.invoke(mapView)
    }

    // Paper plane
    PlaneComposable(
        planeSize = throwViewModel.getPlaneScale(),
        planeState = throwViewModel.planeState,
        planeVisible = showPlane(throwScreenState.uiState)
    )

    // Flight info box
    if (throwScreenState.uiState != ThrowScreenState.ViewingLog) {
        FlightInfoBox(
            context = context,
            throwViewModel = throwViewModel,
        )
    }

    // Circular Slider - only shown when screen state is set to "Throwing"
    if (throwScreenState.uiState == ThrowScreenState.Throwing) {
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
    if (
        throwScreenState.uiState == ThrowScreenState.MovingMap ||
        throwScreenState.uiState == ThrowScreenState.Throwing
    ) {
        ButtonPanel(
            throwScreenState = throwScreenState.uiState,
            throwViewModel = throwViewModel,
            mapView = mapViewState,
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
    if (throwScreenState.uiState == ThrowScreenState.ChoosingPosition) {
        PositionAndHighScoreDrawer(
            context = context,
            throwViewModel = throwViewModel,
            throwScreenState = throwScreenState,
            bottomSheetState = bottomSheetState,
            rowState = rowState,
            scope = scope,
            mapViewState = mapViewState,
            changeLocation = changeLocation,
        )
    }

    // Flight log drawer
    if (throwScreenState.uiState == ThrowScreenState.ViewingLog) {
        FlightLog(
            logState = throwScreenState.logState,
            scaffoldState = scaffoldState,
            centerMap = { pos ->
                mapViewState.controller.animateTo(pos)
            },
            uiState = throwScreenState
        ) {
            throwViewModel.closeLog()
            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
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
        is ThrowScreenState.ViewingLog -> false
    }
    return value
}

/*
 * Code from stackoverflow
 * Rafsanjani answered Dec 20, 2021 at 16:02
 */
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
    var airPressureColor = colRed

    if (throwViewModel.weather.airPressure > 1013) {
        airPressureDescription = stringResource(R.string.high_air_pressure_display)
        airPressureColor = Color(82, 170, 242)
    }

    // Invisible box filling the entire screen
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 20.dp)
            .height(90.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Box containing the info elements
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(14.dp))
                .background(colBlueTransparent),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                // Weather icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.25f)
                ) {
                    Icon(
                        painter = painterResource(id = id),
                        contentDescription = stringResource(R.string.weather_icon_description),
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .fillMaxSize(0.6f)
                    )
                }

                // Air pressure status
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.33f)
                ) {

                    Text(
                        text = airPressureDescription,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = airPressureColor,
                    )
                }

                // Wind direction indicator
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f)
                ){


                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.65f)
                    ){
                        Image(
                            modifier = Modifier
                                //.padding(end = 40.dp, top = 10.dp)
                                .rotate((throwViewModel.weather.windAngle + 180).toFloat())
                                .size(60.dp),
                            painter = painterResource(id = R.drawable.up_arrow__1_),
                            contentDescription = stringResource(R.string.wind_direction_arrow_description),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    Text(
                        text = buildAnnotatedString {

                            withStyle(style = SpanStyle(color = Color.White)) {
                                append("%.1f".format(throwViewModel.weather.windSpeed.toFloat()))
                            }

                            withStyle(style = SpanStyle(color = colGrayLight, fontWeight = FontWeight.Normal)) {
                                append(" m/s")
                            }
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                }

                // Speed and height displays
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                    ){

                        Text(
                            text = stringResource(R.string.speed_display),
                            fontSize = 10.sp,
                            color = colGrayLight
                        )

                        Text(
                            text = "%.1f".format(planeState.speed.toFloat()),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                    }

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {

                        // Height indicator
                        Text(
                            text = stringResource(R.string.height_display),
                            fontSize = 10.sp,
                            color = colGrayLight
                        )

                        Text(
                            text = if (planeState.height >= 0) "%.0f".format(planeState.height) else "0",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonPanel(
    throwScreenState: ThrowScreenState,
    throwViewModel: ThrowViewModel,
    mapView: DisableMapView,
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
                else {
                    mapView.controller.stopAnimation(false)
                    throwViewModel.changeAngle(0.toFloat())
                }
            },
            colors = ButtonDefaults.buttonColors(colRed),
            shape = RoundedCornerShape(20),
        ) {
            Text(
                text = if (throwScreenState == ThrowScreenState.Throwing)
                            stringResource(R.string.throw_string).uppercase()
                       else stringResource(R.string.ready).uppercase(),
                fontSize = 35.sp,
                color = Color.White
            )
        }

        Spacer( Modifier.height(50.dp) )

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
    throwScreenState: ThrowScreenUIState,
    bottomSheetState: SheetState,
    rowState: LazyListState,
    scope: CoroutineScope,
    mapViewState: DisableMapView,
    changeLocation: (locationPoint: GeoPoint, locationName: String) -> Unit,
) {
    val resources = context.resources
    val packageName = context.packageName
    val throwPointWeather = throwScreenState.throwPointWeatherList
    val highScores = throwScreenState.throwPointHighScoreMap
    val highScoreOnMap = throwScreenState.highScoresShownOnMap

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {
            throwViewModel.setThrowScreenState(ThrowScreenState.MovingMap)
        },
        sheetState = bottomSheetState,
        containerColor = colBlueTransparent
    ) {

        Text(
            text = stringResource(R.string.choose_throw_location),
            fontSize = 30.sp,
            color = Color(255, 255, 255, 170),
            modifier = Modifier.padding(start = 15.dp)
        )

        // Cards for the different throw locations
        LazyRow(state = rowState) {
            items(throwPointWeather.size) {

                val location = throwPointWeather[it]
                val id = resources.getIdentifier(location.icon, "drawable", packageName)

                // Clickable card
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = colDarkBlue
                    ),
                    modifier = modifier
                        .fillParentMaxWidth()
                        .padding(15.dp)
                        .height(200.dp),
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
                    Column (
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    ){
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.35f)
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                        ) {
                            // Place name
                            Text(
                                modifier = modifier.padding(start = 12.dp, top = 0.dp),
                                text = location.namePos,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Weather icon
                            Icon(
                                painter = painterResource(id = id),
                                contentDescription = stringResource(R.string.weather_icon_description),
                                modifier = modifier
                                    .padding(end = 12.dp, top = 8.dp)
                                    .size(size = 50.dp),
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
                                text = stringResource(
                                    R.string.temperature_display,
                                    "%.0f".format(location.temperature)
                                ),
                                fontSize = 28.sp
                            )

                            // Precipitation
                            Text(
                                modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                text = stringResource(
                                    R.string.rain_display,
                                    "%.0f".format(location.rain)
                                ),
                                fontSize = 18.sp
                            )

                            // Wind speed
                            Text(
                                modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                text = stringResource(
                                    R.string.wind_speed_display,
                                    "%.0f".format(location.windSpeed)
                                ),
                                fontSize = 18.sp
                            )

                            // Wind direction arrow
                            Icon(
                                painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                                modifier = modifier
                                    .size(size = 45.dp)
                                    .rotate(location.windAngle.toFloat() + 90.toFloat()),
                                contentDescription = stringResource(
                                    R.string.wind_direction_arrow_description,
                                    location.windAngle.toInt()
                                ),
                            )

                            // Air pressure symbol
                            var airPressureDescription =
                                stringResource(R.string.low_air_pressure_display)
                            var airPressureColor = colRed
                            if (location.airPressure > AIR_PRESSURE_NORMAL) {
                                airPressureDescription =
                                    stringResource(R.string.high_air_pressure_display)
                                airPressureColor = Color(82, 170, 242)
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
                                fontWeight = FontWeight.Bold,
                                color = airPressureColor
                            )
                        }

                        // High score banner
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
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

                            Text(
                                text = buildAnnotatedString {

                                    withStyle(style = SpanStyle(color = colGrayLight)) {
                                        append(stringResource(R.string.highscore_display))
                                    }

                                    withStyle(
                                        style = SpanStyle(
                                            color = if (locationName.distance != 0) colGold else colGrayLight,
                                            fontWeight = FontWeight.Bold)
                                    ) {
                                        append(" "+stringResource(id = R.string.km_display, locationName.distance.toString()))
                                    }
                                },
                                fontSize = 16.sp
                            )

                            // Button for toggling if high score path is shown or not
                            Button(
                                modifier = modifier
                                    .height(45.dp)
                                    .width(180.dp),
                                enabled = locationName.distance != 0,
                                onClick = {
                                    if (!highScoreShown) {
                                        drawHighScorePath(
                                            mapViewState.overlays,
                                            locationName.flightPath,
                                            location.namePos
                                        )
                                        drawGoalMarker(
                                            { _, _, _ ->
                                                GoalMarker(
                                                    mapViewState,
                                                    location.namePos,
                                                    highScore = true,
                                                    temporary = true
                                                )
                                            },
                                            mapViewState.overlays,
                                            locationName.flightPath[0],
                                            locationName.locationName,
                                            locationName.flightPath.last(),
                                            newHS = true,
                                            temporary = true
                                        )
                                    } else {
                                        removeHighScorePath(mapViewState.overlays, location.namePos)
                                        mapViewState.invalidate()
                                    }
                                    throwViewModel.updateHighScoreShownState(location.namePos)
                                },
                                colors = ButtonDefaults.buttonColors(colRed),
                                shape = RoundedCornerShape(14),
                            ) {
                                Text(
                                    text = if (!highScoreShown) {
                                        stringResource(R.string.show_highscore)
                                    } else {
                                        stringResource(R.string.hide_highscore)
                                    },
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}