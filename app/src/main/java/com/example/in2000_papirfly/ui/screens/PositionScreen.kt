package com.example.in2000_papirfly.ui.screens

import android.content.Context
import android.content.res.Resources
import android.hardware.usb.UsbDevice.getDeviceId
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.in2000_papirfly.data.Weather
import com.example.in2000_papirfly.ui.viewmodels.PositionScreenViewModel
import io.ktor.http.*
import org.osmdroid.util.GeoPoint

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
                            modifier = modifier.size(size = 70.dp),
                            tint = Color.Unspecified
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            text = "${"%.0f".format(location.temperature)}°C",
                            fontSize = 30.sp
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            text = "${"%.0f".format(location.rain)}mm",
                            fontSize = 20.sp
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            text = "${"%.0f".format(location.windSpeed)}m/s",
                            fontSize = 20.sp
                        )

                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                            modifier = modifier
                                .size(size = 45.dp)
                                .rotate(location.windAngle.toFloat() + 90.toFloat()),
                            contentDescription = "Vind retning",
                        )
                    }
                }
            }
        }
    }
}