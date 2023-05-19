package com.example.in2000_papirfly.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.in2000_papirfly.PapirflyApplication


@Composable
fun PlaneProvider() {

    val loadOutRepository = (LocalContext.current.applicationContext as PapirflyApplication).appContainer.loadOutRepository

    PlaneRender(
        paper = loadOutRepository.getAttachmentInSlot(0),
        nose = loadOutRepository.getAttachmentInSlot(1),
        wings = loadOutRepository.getAttachmentInSlot(2),
        tail = loadOutRepository.getAttachmentInSlot(3)
    )
}
