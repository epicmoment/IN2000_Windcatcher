package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.in2000_papirfly.data.Location
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.MapView
import org.osmdroid.util.GeoPoint

@Composable
fun ThrowScreen(selectedLocation : GeoPoint) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        //Text(text = "Throw Screen wow!")
        //Text(text = "${selectedLocation}")
        MapView(
            location = selectedLocation
        )
    }
}