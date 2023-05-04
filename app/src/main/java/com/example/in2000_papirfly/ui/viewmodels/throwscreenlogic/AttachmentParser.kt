package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.*

fun addAttachments(planeRepository: PlaneRepository, loadoutRepository: LoadoutRepository){
    val loadout = loadoutRepository.loadoutState.value

    // todo // endre Attachment-klassen til å inneholde et FlightModifier objekt vi kan bruke?

    // Paper types
    val attachmentIndex = loadout.slot1attachment ?: 0
    val attachmentName = Attachments.list[0][attachmentIndex].name

    var flightModifier = when{
        attachmentName == "Skrivepapir" -> {
            FlightModifier(
                rainEffect = 0.5,
                windEffect = 0.75,
            )
        }
        attachmentName == "Fotopapir" -> FlightModifier(
            windEffect = 0.0
        )
        else -> FlightModifier()
    }

    val plane = planeRepository.planeState.value
    planeRepository.update(
        plane.copy(
            flightModifier = flightModifier
        )
    )
}