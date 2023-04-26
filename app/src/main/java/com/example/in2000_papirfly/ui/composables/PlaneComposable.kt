package com.example.in2000_papirfly.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.Plane
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PlaneComposable(planeSize: Float, planeState: StateFlow<Plane>, planeVisible: Boolean){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        val plane = planeState.collectAsState().value
        var alpha = if (planeVisible) 1.0f else 0.0f
        Image(
            painter = painterResource(id = R.drawable.plane01),
            contentDescription = "TODO",
            modifier = Modifier
                .rotate((plane.angle).toFloat())
                .scale(planeSize),
            alpha = alpha
        )
    }
}