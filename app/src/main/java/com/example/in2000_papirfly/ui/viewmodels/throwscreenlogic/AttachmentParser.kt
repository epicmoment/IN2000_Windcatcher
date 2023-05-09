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
    // TODO
    // Problem of too long flights seem to happen here, it only picks up the first flightmodifier it seems
    for ((i, index ) in loadout.slots.withIndex()){
        Log.d("i", i.toString())
        Log.d("index", index.toString())
        flightModifiers.add(Attachments.list[i][index].flightModifier )
    }

    var finalFlightModifier = FlightModifier(weight = 0.0, slowRateEffect = 0.0)  // Setting weight and slowRateEffect to 0.0 here so we don't get 5 x 0.25 for these values
    for (flightModifier in flightModifiers){
        finalFlightModifier = combineFlightModifiers(finalFlightModifier, flightModifier)
    }

    val plane = planeRepository.planeState.value
    planeRepository.update(
        plane.copy(
            flightModifier = finalFlightModifier
        )
    )

    Log.d("weight", finalFlightModifier.weight.toString())
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
    val weight = flightModifier1.weight + flightModifier2.weight
    val slowRateEffect = flightModifier1.slowRateEffect + flightModifier2.slowRateEffect

    return FlightModifier(windEffect, airPressureEffect, rainEffect, temperatureEffect, weight, slowRateEffect)
}