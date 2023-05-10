package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.ui.theme.colOrange

@Composable
fun MainScreen(onNextPage : () -> Unit, onCustomizePage : () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.kartbl_2),
                contentScale = ContentScale.Crop,
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.paperplane),
                contentDescription = "Paperplane Image",
                modifier = Modifier
                    .size(size = 250.dp),
            )

            Button (
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                ),
                onClick = onNextPage,
                colors = ButtonDefaults.buttonColors(colOrange),
                shape = RoundedCornerShape(10),
            ) {
                Text(
                    text = "KAST",
                    fontSize = 40.sp,
                    //color = Color.White
                )
            }

            /*Button (
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                ),
                onClick = onLogScreenPage,
                colors = ButtonDefaults.buttonColors(colOrange),
                shape = RoundedCornerShape(8),
            ) {
                Text(
                    text = "LOG :)",
                    fontSize = 25.sp,
                    color = Color.White
                )
            }*/

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button (
                    onClick = onCustomizePage,
                    colors = ButtonDefaults.buttonColors(colOrange),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .shadow(elevation = 0.dp,
                            ambientColor = Color.Black,
                            spotColor = Color.Black),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.construction),
                        contentDescription = "Customize Page",
                        modifier = Modifier.size(size = 30.dp),
                        tint = Color.White
                    )
                }

                /*Button (
                    onClick = onNextPage,
                    colors = ButtonDefaults.buttonColors(colOrange),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .shadow(elevation = 0.dp,
                            ambientColor = Color.Black,
                            spotColor = Color.Black),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.construction),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(size = 30.dp),
                        tint = Color.White
                    )
                }

                Button (
                    onClick = onNextPage,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(colOrange),
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .shadow(elevation = 0.dp,
                            ambientColor = Color.Black,
                            spotColor = Color.Black),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.construction),
                        contentDescription = "",
                        modifier = Modifier.size(size = 30.dp),
                        tint = Color.White,
                    )
                }*/
            }
        }
    }
}