package com.example.in2000_papirfly.ui.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import org.osmdroid.views.overlay.Marker

@SuppressLint("DiscouragedApi")
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ThrowScreen(
    modifier: Modifier = Modifier,
    selectedLocation : GeoPoint,
    locationName: String,
    onLoad: ((map: MapView) -> Unit)? = null,
    weatherRepository: DataRepository,
    planeRepository: PlaneRepository,
    onBack: () -> Unit
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val mapViewState = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    // TODO
    // I'm making a new ThrowViewModel object here that should be made somewhere else and injected
    val throwViewModel = remember{
        ThrowViewModel(
            locationName,
            selectedLocation,
            { Marker(mapViewState) },
            mapViewState.overlays,
            mapViewState.controller,
            { inputUpdate: () -> Unit
                -> mapViewState.updateOnMoveMap {
                inputUpdate()
            }
            },
            { interactionEnabled: Boolean
                -> mapViewState.setInteraction(interactionEnabled)
            },
            planeRepository = planeRepository,
            weatherRepository = weatherRepository,
        )
    }
    val throwPointWeather: List<Weather> = throwViewModel.throwPointWeather
    val highScore = throwViewModel.highScore.collectAsState()
    val throwScreenState = throwViewModel.getThrowScreenState()

    BackHandler {
        Log.d("ThrowScreen", "Back press detected")
        throwViewModel.planeFlying.cancel()
        planeRepository.update(Plane())
        onBack()
    }
    // Map composable
    AndroidView(
        { mapViewState },
        Modifier,
    ) { mapView -> onLoad?.invoke(mapView) }

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
        Text(text = "Local highscore at ${highScore.value.locationName}: ${highScore.value.distance}km")

        Text(
            text = "Height: ${"%.0f".format(throwViewModel.planeState.collectAsState().value.height)}")

        Button(
//            enabled = !throwViewModel.planeState.collectAsState().value.flying,
            enabled = !throwViewModel.flyingState,
            onClick = {
                throwViewModel.throwPlane()
            }
        ){
            Text("Throw")
        }

        //TODO remove
        Button(
            onClick = {
                openBottomSheet = !openBottomSheet
            }
        ){
            Text("Test")
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
            LazyRow {
                items(throwPointWeather.size) {
                    val location = throwPointWeather[it]
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(15.dp),
//                            .clickable {
//
//                            },
                        onClick = {
                            mapViewState.controller.animateTo(ThrowPointList.throwPoints[throwPointWeather[it].namePos])

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