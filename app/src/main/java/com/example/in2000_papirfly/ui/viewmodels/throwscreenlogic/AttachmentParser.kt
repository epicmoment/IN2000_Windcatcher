package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.*

fun addAttachments(planeRepository: PlaneRepository, loadoutRepository: LoadoutRepository){
    val loadout = loadoutRepository.loadoutState.value
    val flightModifiers: MutableList<FlightModifier> = mutableListOf()

    // Paper
    val attachmentIndex = loadout.slot1attachment ?: 0
    flightModifiers.add(Attachments.list[0][attachmentIndex].flightModifier )

    // Nose


    // Wing


    // Fin
    

    var finalFlightModifier = FlightModifier()
    for (flightModifier in flightModifiers){
        finalFlightModifier = combineFlightModifiers(finalFlightModifier, flightModifier)
    }

    val plane = planeRepository.planeState.value
    planeRepository.update(
        plane.copy(
            flightModifier = finalFlightModifier
        )
    )
}

fun combineFlightModifiers(flightModifier1: FlightModifier, flightModifier2: FlightModifier): FlightModifier{
    // This should be done differently so you can expand the system by only adding values to the FlightModifier class

    val windEffect = flightModifier1.windEffect + flightModifier2.windEffect
    val airPressureEffect = flightModifier1.airPressureEffect + flightModifier2.airPressureEffect
    val rainEffect = flightModifier1.rainEffect + flightModifier2.rainEffect
    val temperatureEffect = flightModifier1.temperatureEffect + flightModifier2.temperatureEffect

    return FlightModifier(windEffect, airPressureEffect, rainEffect, temperatureEffect)
}