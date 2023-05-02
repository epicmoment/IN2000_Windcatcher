package com.example.in2000_papirfly.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.R

@Composable
fun PaperFrame(){
    Image(
        painter = painterResource(id = R.drawable.frame),
        contentDescription = "Todo",
        contentScale = ContentScale.FillBounds
    )
}