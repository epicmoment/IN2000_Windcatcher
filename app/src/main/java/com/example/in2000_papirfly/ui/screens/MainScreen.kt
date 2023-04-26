package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.in2000_papirfly.R

@Composable
fun MainScreen(onNextPage : () -> Unit, onCustomizePage : () -> Unit ) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                onNextPage()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Papirflyapp!",
                fontSize = 28.sp
            )

            Image(
                painter = painterResource(id = R.drawable.plane01),
                contentDescription = "papirfly")

            Text(
                text = "Press to start",
                fontSize = 15.sp
            )

            Button(
                onClick = onCustomizePage,
            ) {
                Text(text = "Customize")
            }

        }
    }
}