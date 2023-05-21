package no.met.in2000.windcatcher.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import no.met.in2000.windcatcher.WindcatcherApplication

@Composable
fun PlaneProvider() {

    val loadOutRepository =
        (LocalContext.current.applicationContext as WindcatcherApplication)
            .appContainer
            .loadOutRepository

    PlaneRender(
        paper = loadOutRepository.getAttachmentInSlot(0),
        nose = loadOutRepository.getAttachmentInSlot(1),
        wings = loadOutRepository.getAttachmentInSlot(2),
        tail = loadOutRepository.getAttachmentInSlot(3)
    )
}
