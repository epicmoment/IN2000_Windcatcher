package com.example.in2000_papirfly.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.in2000_papirfly.data.Plane
import org.w3c.dom.Text

@Composable
fun ThrowScreen(){
    val plane = remember{ Plane() }
    Text(
        text = "Throw screen"
    )

    Box(
        modifier = Modifier
            .width(10.dp)
            .height(10.dp)
            .offset(plane.pos[0].dp, plane.pos[1].dp)
            .background(Color.Cyan)
    ){

    }
}