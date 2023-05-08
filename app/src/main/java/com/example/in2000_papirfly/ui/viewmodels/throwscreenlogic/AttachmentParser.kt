package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import android.util.Log
import com.example.in2000_papirfly.data.*

/**
 * Updates the plane in the PlaneRepository to have a FlightModifier that is the result of all the
 * attachments on the plane.
 */
fun addAttachments(planeRepository: PlaneRepository, loadoutRepository: LoadoutRepository){
    val loadout = loadoutRepository.loadoutState.value
    val flightModifiers: MutableList<FlightModifier> = mutableListOf()

    /*
    // Paper
    var attachmentIndex = loadout.slots[0]
    flightModifiers.add(Attachments.list[0][attachmentIndex].flightModifier )

    // Nose
    attachmentIndex = loadout.slots[1]
    flightModifiers.add(Attachments.list[0][attachmentIndex].flightModifier )


    // Wing


    // Fin

    */
    
    // Add all flight modifiers in the attached attachments
    for ((i, index ) in loadout.slots.withIndex()){
        flightModifiers.add(Attachments.list[i][index].flightModifier )
    }


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

/**
 * Returns a FlightModifier where each effect gets the value of the sum of flightModifier1's and
 * flightModifier2's respective effect values.
 */
fun combineFlightModifiers(flightModifier1: FlightModifier, flightModifier2: FlightModifier): FlightModifier{
    // This should be done differently so you can expand the system by only adding values to the FlightModifier class

    val windEffect = flightModifier1.windEffect + flightModifier2.windEffect
    val airPressureEffect = flightModifier1.airPressureEffect + flightModifier2.airPressureEffect
    val rainEffect = flightModifier1.rainEffect + flightModifier2.rainEffect
    val temperatureEffect = flightModifier1.temperatureEffect + flightModifier2.temperatureEffect

    return FlightModifier(windEffect, airPressureEffect, rainEffect, temperatureEffect)
}