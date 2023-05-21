package no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic

import android.util.Log
import no.met.in2000.windcatcher.data.components.FlightModifier
import no.met.in2000.windcatcher.data.repositories.LoadOutRepository
import no.met.in2000.windcatcher.data.repositories.PlaneRepository

/**
 * Updates the plane in the PlaneRepository to have a FlightModifier that is the result of all the
 * attachments on the plane.
 */
fun addAttachments(planeRepository: PlaneRepository, loadOutRepository: LoadOutRepository){

    val flightModifiers: MutableList<FlightModifier> = mutableListOf()

    // Add all flight modifiers in the attached attachments
    repeat(4) {
        flightModifiers.add(loadOutRepository.getAttachmentInSlot(it).flightModifier)
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
fun combineFlightModifiers(flightModifier1: FlightModifier, flightModifier2: FlightModifier): FlightModifier {
    // This should be done differently so you can expand the system by only adding values to the FlightModifier class
    // FlightModifier::class.typeParameters

    val windEffect = flightModifier1.windEffect + flightModifier2.windEffect
    val airPressureEffect = flightModifier1.airPressureEffect + flightModifier2.airPressureEffect
    val rainEffect = flightModifier1.rainEffect + flightModifier2.rainEffect
    val temperatureEffect = flightModifier1.temperatureEffect + flightModifier2.temperatureEffect
    val weight = flightModifier1.weight + flightModifier2.weight
    val slowRateEffect = flightModifier1.slowRateEffect + flightModifier2.slowRateEffect

    return FlightModifier(windEffect, airPressureEffect, rainEffect, temperatureEffect, weight, slowRateEffect)
}