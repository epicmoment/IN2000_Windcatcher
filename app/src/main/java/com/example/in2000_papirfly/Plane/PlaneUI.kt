package com.example.in2000_papirfly.Plane

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.R


@Composable
fun PlaneElement(plane: Plane){
    val smoothPos = remember{plane.pos}

    Image(
        painter = painterResource(id = R.drawable.plane01),
        contentDescription = "TODO",
        modifier = Modifier
            .rotate(plane.angle.toFloat())
            //.scale(throwViewModel.getPlaneScale())
    )

}
class PlaneUI {
    // This class should have the composable for the plane, and the logic for drawing smoothly between states
}