package no.met.in2000.windcatcher.ui.composables

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.met.in2000.windcatcher.R
import no.met.in2000.windcatcher.data.screenuistates.LogPoint
import no.met.in2000.windcatcher.data.screenuistates.LogState
import no.met.in2000.windcatcher.data.screenuistates.ThrowScreenState
import no.met.in2000.windcatcher.data.screenuistates.ThrowScreenUIState
import no.met.in2000.windcatcher.ui.theme.*
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
        Log.d("Log", "LogState: ${scaffoldState.bottomSheetState.currentValue}")
        if (
            scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden &&
            uiState.uiState is ThrowScreenState.ViewingLog
        ) {
            onBack()
        }
    }

    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 180.dp,
        sheetContainerColor = colBlueTransparent,
        sheetContent = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {

                Box(
                    contentAlignment = Alignment.Center,
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

                        Box(
                            modifier = Modifier.clickable {
                                scope.launch {
                                    //scaffoldState.bottomSheetState.partialExpand()
                                    centerMap(logState.logPoints[it].geoPoint)
                                }
                            }
                        ) {
                            PathEntryCard(
                                showTopLine = it > 0,
                                showBottomLine = it < logState.logPoints.size - 1,
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
    logPoint : LogPoint
) {

    val weather = logPoint.weather

    // Padding
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
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
                    .height(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colDarkBlue)
            ) {

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 10.dp)
                ){

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

                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(45.dp)
                            .fillMaxHeight()
                    ){
                        Image(
                            modifier = Modifier
                                .rotate((weather.windAngle + 180).toFloat())
                                .size(40.dp),
                            painter = painterResource(id = R.drawable.up_arrow__1_),
                            contentDescription = stringResource(R.string.wind_direction_arrow_description),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = weather.windSpeed.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text (
                            text = "m/s",
                            color = colGray
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = weather.rain.toString(),
                            color = if (weather.rain > 0) {
                                Color(85, 147, 198)
                            } else {
                                colGray
                            },
                            fontWeight = if (weather.rain > 0) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                        Text (
                            text = "mm",
                            color = colGray
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {

                        // Speed
                        Text(
                            text = buildAnnotatedString {

                                withStyle(style = SpanStyle(color = colGray)) {
                                    append("F ")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        color = when {
                                            logPoint.speed > 20 -> Color(92, 196, 104)
                                            logPoint.speed > 5 -> Color.White
                                            else -> colRed
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(
                                        "%.1f".format(logPoint.speed)
                                    )
                                }
                            },
                            fontSize = 14.sp
                        )

                        // Height
                        Text(
                            text = buildAnnotatedString {

                                withStyle(style = SpanStyle(color = colGray)) {
                                    append("H ")
                                }

                                withStyle(
                                    style = SpanStyle(
                                        color = if (logPoint.height>20) Color.White else colRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(
                                        if (logPoint.height > 0)"%.0f".format(logPoint.height) else "0"
                                    )
                                }
                            },
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
