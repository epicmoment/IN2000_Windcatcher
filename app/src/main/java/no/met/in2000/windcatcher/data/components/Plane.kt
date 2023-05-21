package no.met.in2000.windcatcher.data.components

data class Plane(
    val flightModifier: FlightModifier = FlightModifier(),
    val pos : List<Double> = listOf(59.943325914913615, 10.717908529673489),
    val flying : Boolean = false,
    /** Given in degrees */
    val angle : Double = 0.0,
    val speed : Double = 0.0,
    val height : Double = 100.0
)

/**
 * This class is used in PlaneLogic when the effects of the weather is calculated, and are usually a number between 0 and 1
 */
data class FlightModifier(
    /** How much effect the wind vector has on the plane's vector (both angle and speed) */
    val windEffect: Double = 0.0,

    /** How much the air pressure effects the drop rate of the plane.
     *
     * Positive for gaining height in high air pressure, negative gaining height in low air pressure
     */
    val airPressureEffect: Double = 0.0,

    /** How much the rainfall effects the drop rate of the plane.
     *
     * Positive for higher drop rate, and negative for gaining height in rain*/
    val rainEffect: Double = 0.0,

    /**
     * Positive for gaining height in temperature above 0 and losing height in temperatures below 0.
     * Negative for losing height in temperature above 0 and gaining height in temperatures below 0.
     */
    val temperatureEffect: Double = 0.0,

    /**
     * Affects how much the plane drops every update. Used as a general balancing value
     * Default value is 0.25 (0.25 per Attachment makes 1 for the whole plane.
     */
    val weight: Double = 0.25,

    /**
     * Affects how much speed the plane loses every update.
     * Default value is 0.25 (0.25 per Attachment makes 1 for the whole plane.
     */
    val slowRateEffect: Double = 0.25,

    // Ideas here are:
    // sunDropRateEffect
    // sideWindEffect
    // tailwindEffect
    // headwindEffect
){
    //val effects = listOf<Double>(windEffect, airPressureEffect, rainEffect, temperatureEffect)
}
