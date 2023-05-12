package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.in2000_papirfly.ui.theme.colRed
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempLogScreen ( onBack : () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(180, 180, 180))
            .clickable { onBack() }
    )


    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 180.dp,
        sheetContainerColor = Color(0, 20, 50, 100),
        sheetContent = {

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                ) {

                    items(10) {

                        Box (
                            modifier = Modifier.clickable {
                                scope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        ) {
                            PathEntryCard(
                                i = it,
                                showTopLine = it > 0,
                                showBottomLine = it < 9
                            )
                        }

                    }

                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )

            }

        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color(100, 200, 140))
        ) {
            Text("Map her")
        }
    }

}

@Composable
fun PathEntryCard(i : Int, showTopLine : Boolean, showBottomLine : Boolean) {

    // Paddingbeholder
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

            // Sirkelholder
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

                    // Top Line
                    Box (
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(7.dp)
                            .background(
                                if (showTopLine) {colRed} else {Color.Transparent}
                            )
                    )

                    // Bottom Line
                    Box (
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(7.dp)
                            .background(
                                if (showBottomLine) {colRed} else {Color.Transparent}
                            )
                    )
                }

                // Sirkel
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

                Text(i.toString())

            }

        }

    }

}
