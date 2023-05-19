package com.example.in2000_papirfly.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import com.example.in2000_papirfly.data.components.Plane
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PlaneComposable(planeSize: Float, planeState: StateFlow<Plane>, planeVisible: Boolean){

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        val plane = planeState.collectAsState().value
        val alpha = if (planeVisible) 1.0f else 0.0f

        Box (
            modifier = Modifier
                .rotate((plane.angle).toFloat())
                .scale(planeSize * 0.25f)
                .alpha(alpha)

        ) {

            PlaneProvider()

        }

    }
}