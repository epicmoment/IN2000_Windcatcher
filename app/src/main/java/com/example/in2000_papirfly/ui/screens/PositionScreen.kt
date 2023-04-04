package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.Location
import com.example.in2000_papirfly.data.Weather
import com.example.in2000_papirfly.data.WeatherRepositoryMVP
import com.example.in2000_papirfly.ui.viewmodels.PositionScreenViewModel
import org.osmdroid.util.GeoPoint
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionScreen(
    viewModel: PositionScreenViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onNextPage : (GeoPoint) -> Unit,
    getWeather: (String) -> Weather
) {

    val posScrUiState = viewModel.posScrUiState.collectAsState()

    val locationList = listOf("Oslo", "Stavanger", "Galdhøpiggen")

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = modifier
            .fillMaxSize()) {
            //items(posScrUiState.value.weather.size){
            items(locationList.size) {
                val location = getWeather(locationList[it])
                Card(
                    shape = MaterialTheme.shapes.medium,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(15.dp)
                        .clickable {
                            onNextPage(GeoPoint(posScrUiState.value.weather[it].geoPoint))
                        }
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = "${posScrUiState.value.weather[it].namePos}",
                        fontSize = 30.sp
                    )

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.clear_day_48px),
                            modifier = modifier.size(size = 70.dp),
                            contentDescription = "Full sol",
                            tint = Color.Yellow
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            text = "${"%.0f".format(location.temperature)}°C",
                            //text = "${"%.0f".format(posScrUiState.value.weather[it].temperature)}°C" ,
                            //text = "25°C",
                            fontSize = 30.sp
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            text = "${"%.0f".format(location.windSpeed)}m/s",
                            //text = "${"%.0f".format(posScrUiState.value.weather[it].windSpeed)}m/s",
                            //text = "4(6) m/s",
                            fontSize = 20.sp
                        )

                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                            modifier = modifier
                                .size(size = 45.dp)
                                .rotate(location.windAngle.toFloat() + 90.toFloat()),
                                //.rotate(posScrUiState.value.weather[it].windFromDirection.toFloat()),
                            contentDescription = "Vind retning",
                        )
                    }
                }
            }
        }
    }
}