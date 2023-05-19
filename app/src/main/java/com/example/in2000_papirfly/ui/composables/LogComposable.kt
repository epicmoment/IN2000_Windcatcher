package com.example.in2000_papirfly.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_papirfly.data.components.Weather
import com.example.in2000_papirfly.data.screenuistates.LogState
import com.example.in2000_papirfly.data.screenuistates.ThrowScreenState
import com.example.in2000_papirfly.data.screenuistates.ThrowScreenUIState
import com.example.in2000_papirfly.ui.theme.colBlueTransparent
import com.example.in2000_papirfly.ui.theme.colRed
import com.example.in2000_papirfly.ui.theme.colDarkBlue
import com.example.in2000_papirfly.ui.theme.colGold
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightLog (
    logState: LogState,
    scaffoldState: BottomSheetScaffoldState,
    centerMap: (GeoPoint) -> Unit,
    uiState: ThrowScreenUIState,
    onBack : () -> Unit
) {

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden && uiState.uiState is ThrowScreenState.ViewingLog) {
            onBack()
        }
    }

    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 180.dp,
        sheetContainerColor = colBlueTransparent,
        sheetContent = {

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {

                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = logState.distance.toString() + " km",
                        color = if (logState.newHS) colGold else Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                onBack()
                                scope.launch {
                                    scaffoldState.bottomSheetState.hide()
                                }
                            }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                ) {

                    items(logState.logPoints.size) {

                        Box (
                            modifier = Modifier.clickable {
                                scope.launch {
                                    //scaffoldState.bottomSheetState.partialExpand()
                                    centerMap(logState.logPoints[it].first)
                                }
                            }
                        ) {
                            PathEntryCard(
                                showTopLine = it > 0,
                                showBottomLine = it < logState.logPoints.size-1,
                                logPoint = logState.logPoints[it]
                            )
                        }

                    }

                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )

            }

        }

    ) {

    }

}

@SuppressLint("DiscouragedApi")
@Composable
fun PathEntryCard(
    showTopLine : Boolean,
    showBottomLine : Boolean,
    logPoint : Pair<GeoPoint, Weather>
) {

    val weather = logPoint.second

    // Padding
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ){

            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(40.dp)
            ) {

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Box (
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(7.dp)
                            .background(
                                if (showTopLine) {
                                    colRed
                                } else {
                                    Color.Transparent
                                }
                            )
                    )

                    Box (
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(7.dp)
                            .background(
                                if (showBottomLine) {
                                    colRed
                                } else {
                                    Color.Transparent
                                }
                            )
                    )
                }

                Canvas(
                    modifier = Modifier.size(25.dp),
                    onDraw = {
                        drawCircle(
                            radius = 35f,
                            color = colRed
                        )
                        drawCircle(
                            radius = 20f,
                            color = Color.White
                        )
                    }
                )

            }

            // Informasjonskort
            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colDarkBlue)
            ) {


                Row (
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()

                ){

                    Text(weather.windSpeed.toString())

                    Image(
                        painter = painterResource(
                            id = LocalContext.current.resources.getIdentifier(
                                weather.icon,
                                "drawable",
                                LocalContext.current.packageName
                            )
                        ),
                        contentDescription = "ikon"
                    )

                }


            }

        }

    }

}
