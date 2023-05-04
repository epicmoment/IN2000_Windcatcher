package com.example.in2000_papirfly.ui.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_papirfly.R
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.composables.PlaneComposable
import com.example.in2000_papirfly.ui.viewmodels.ThrowScreenState
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import org.osmdroid.util.GeoPoint
import io.ktor.util.reflect.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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

    val throwPointWeather: List<Weather> = throwViewModel.throwPointWeather
//    val highScore = throwViewModel.highScore.collectAsState()
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
        Modifier,
    ) { mapView -> onLoad?.invoke(mapView) }

    // This fixes the map glitching
    mapViewState.controller.setCenter(throwViewModel.previousPlanePos)
    mapViewState.controller.setZoom(12.0)

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
//        Text(text = "Local highscore at ${highScore.value.locationName}: ${highScore.value.distance}km")

        Text(
            text = "Height: ${"%.0f".format(throwViewModel.planeState.collectAsState().value.height)}")

        Button(
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
            enabled = !throwViewModel.flyingState
        )
    }

//    Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,

        ) {
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//                Button(
//                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
//                    // you must additionally handle intended state cleanup, if any.
//                    onClick = {
//                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
//                            if (!bottomSheetState.isVisible) {
//                                openBottomSheet = false
//                            }
//                        }
//                    }
//                ) {
//                    Text("Hide Bottom Sheet")
//                }
//            }
            LazyRow(state = rowState) {
                items(throwPointWeather.size) {
                    val location = throwPointWeather[it]
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        onClick = {
                            scope.launch {
                                rowState.animateScrollToItem(it)
                            }
                            val newLocation = ThrowPointList.throwPoints[throwPointWeather[it].namePos]
                            throwViewModel.previousPlanePos = mapViewState.mapCenter as GeoPoint
//                            mapViewState.controller.zoomTo(12.0)
                            mapViewState.controller.animateTo(newLocation, 12.0, 1000)
                            throwViewModel.moveLocation(newLocation!!, throwPointWeather[it].namePos!!)
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
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Highscore: ${highScores.value[location.namePos]!!.distance}km")

                            Button (
//                                modifier = Modifier.shadow(
//                                    elevation = 10.dp,
//                                    ambientColor = Color.Black,
//                                    spotColor = Color.Black
//                                ),
                                enabled = highScores.value[location.namePos]!!.distance != 0,
                                onClick = {
                                    if (!highScoreOnMap.value[location.namePos]!!) {
                                        drawHighScorePath(mapViewState.overlays, highScores.value[location.namePos]!!.flightPath!!, location.namePos!!)
                                        drawGoalMarker(
                                            { HighScoreMarker(mapViewState, location.namePos) },
                                            mapViewState.overlays,
                                            highScores.value[location.namePos]!!.flightPath!![0],
                                            highScores.value[location.namePos]!!.flightPath!!.last(),
                                            true
                                        )
                                    } else {
                                        removeHighScorePath(mapViewState.overlays, location.namePos!!)
                                        mapViewState.invalidate()
                                    }
                                    throwViewModel.updateHighScoreShownState(location.namePos)
                                    Log.d("HighScore", "HighScore shown? ${highScoreOnMap.value[location.namePos]!!}")
                                },
                                colors = ButtonDefaults.buttonColors(com.example.in2000_papirfly.ui.theme.colOrange),
                                shape = RoundedCornerShape(10),
                            ) {
                                Text(
                                    text = if (!highScoreOnMap.value[location.namePos]!!) "Vis highscore" else "Skjul highscore",
                                    fontSize = 10.sp,
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