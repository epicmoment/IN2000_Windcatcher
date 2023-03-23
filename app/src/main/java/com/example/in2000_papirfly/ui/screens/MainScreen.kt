package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(onNextPage : () -> Unit) {

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
                text = "papirflyapp!",
                fontSize = 28.sp
            )

            Text(text = "wow trykk for å starte!")

        }

    }




}