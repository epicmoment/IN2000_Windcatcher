package com.example.in2000_papirfly.ui


import android.util.Half.toFloat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.in2000_papirfly.Plane.*
import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.Location
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.serialization.internal.throwMissingFieldException

@Composable
fun ThrowScreen(selectedLocation : Location) {
    // TODO
    // I'm making a new ThrowViewModel object here that should be made somewhere else and injected
    val throwViewModel = remember{ThrowViewModel()}


    // Wind arrow
    Image(
        painter = painterResource(id = R.drawable.arrow01),
        contentDescription = "TODO",
        modifier = Modifier
            .rotate(
                throwViewModel
                    .getWindAngle()
                    .toFloat()
            )
            .scale(0.2f)
    )


    Column(){
        Text(text = "Throw Screen wow!")
        Text(text = "Wind angle: ${throwViewModel.getWindAngle().toFloat()}")
        Text(text = "Plane angle: ${throwViewModel.getPlaneAngle().toFloat()}")
        Text(text = "Plane pos: \n${throwViewModel.getPlanePos()[0].toFloat()}\n${throwViewModel.getPlanePos()[1].toFloat()}")

        // Paper plane
        Box(
            modifier = Modifier
                .offset(throwViewModel.getPlanePos()[0].dp, throwViewModel.getPlanePos()[1].dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plane01),
                contentDescription = "TODO",
                modifier = Modifier
                    .rotate(throwViewModel.getPlaneAngle().toFloat())
                    .scale(throwViewModel.getPlaneScale())
            )
        }


        Text(
            text = "Height: ${throwViewModel.getPlaneHeight()}}")


        Button(
            onClick = {
                throwViewModel.throwPlane(selectedLocation);
            }
        ){
            Text("Throw")
        }

    }
}