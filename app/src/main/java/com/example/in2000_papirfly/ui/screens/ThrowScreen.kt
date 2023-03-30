package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.in2000_papirfly.data.Location
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.MapView
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.rememberMapViewWithLifecycle
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun ThrowScreen(
    selectedLocation : GeoPoint,
    onLoad: ((map: MapView) -> Unit)? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        //Text(text = "Throw Screen wow!")
        //Text(text = "${selectedLocation}")
        val mapViewState = rememberMapViewWithLifecycle()

        AndroidView(
            { mapViewState },
            Modifier
        ) { mapView -> onLoad?.invoke(mapView) }

        mapViewState.controller.setCenter(selectedLocation)
    }
}