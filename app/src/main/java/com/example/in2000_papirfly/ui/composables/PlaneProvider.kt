package com.example.in2000_papirfly.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.in2000_papirfly.PapirflyApplication


@Composable
fun PlaneProvider() {

    val loadoutRepository = (LocalContext.current.applicationContext as PapirflyApplication).appContainer.loadoutRepository

    PlaneRender(
        paper = loadoutRepository.getAttachmentInSlot(0),
        nose = loadoutRepository.getAttachmentInSlot(1),
        wings = loadoutRepository.getAttachmentInSlot(2),
        tail = loadoutRepository.getAttachmentInSlot(3)
    )

}
