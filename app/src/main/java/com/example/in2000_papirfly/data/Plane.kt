package com.example.in2000_papirfly.data

import java.util.Vector
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

data class Plane(
    val flightModifier: FlightModifier = FlightModifier(),
    val pos : List<Double> = listOf(59.943325914913615, 10.717908529673489),
    val flying : Boolean = false,
    val angle : Double = 45.0,  // given in degrees
    val speed : Double = 0.0,
    val height : Double = 100.0
)

data class FlightModifier(
    // This class is used in PlaneLogic when the effects of the weather is calculated, and are usually a number between 0 and 1
    val windEffect: Double = 0.2

    // Ideas here are:
    // rainDropRateEffect
    // sunDropRateEffect
    // sideWindEffect
    // tailwindEffect
    // headwindEffect

)
