package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.in2000_papirfly.data.Location

@Composable
fun PositionScreen(onNextPage : (Location) -> Unit) {

    Text(text = "Position Screen wow!")
    Button(
        onClick = {
            onNextPage(Location(0.0, 0.0))
        }
    ) {

        Text("Neste side!")

    }
}