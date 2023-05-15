package com.example.in2000_papirfly.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.example.in2000_papirfly.data.Attachment

@Composable
fun PlaneRender(
    paper : Attachment,
    nose : Attachment,
    wings : Attachment,
    tail : Attachment
) {

    Box (
        modifier = Modifier
            .fillMaxSize()
    ) {

        val tint = paper.tint

        Image(
            painter = painterResource(id = tail.icon),
            contentDescription = "Halefinne",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(color = tint, blendMode = BlendMode.Modulate)
        )

        Image(
            painter = painterResource(id = wings.icon),
            contentDescription = "Vinge",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(color = tint, blendMode = BlendMode.Modulate)
        )

        Image(
            painter = painterResource(id = nose.icon),
            contentDescription = "Nese",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(color = tint, blendMode = BlendMode.Modulate)
        )

    }

}