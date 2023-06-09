package no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic

import androidx.lifecycle.ViewModel
import no.met.in2000.windcatcher.data.repositories.PlaneRepository
import no.met.in2000.windcatcher.data.components.Weather
import no.met.in2000.windcatcher.data.components.FlightModifier
import no.met.in2000.windcatcher.helpers.Vector
import no.met.in2000.windcatcher.helpers.Vector.Companion.addVectors
import no.met.in2000.windcatcher.helpers.Vector.Companion.calculateAngle
import no.met.in2000.windcatcher.helpers.Vector.Companion.calculateVector
import no.met.in2000.windcatcher.helpers.Vector.Companion.multiplyVector
import no.met.in2000.windcatcher.helpers.Vector.Companion.vectorLength
import no.met.in2000.windcatcher.helpers.WeatherConstants.AIR_PRESSURE_MAX
import no.met.in2000.windcatcher.helpers.WeatherConstants.AIR_PRESSURE_MIN
import no.met.in2000.windcatcher.helpers.WeatherConstants.RAIN_MAX
import no.met.in2000.windcatcher.helpers.WeatherConstants.TEMPERATURE_MAX
import no.met.in2000.windcatcher.helpers.WeatherConstants.TEMPERATURE_MIN
import org.osmdroid.util.GeoPoint
import kotlin.math.*
/**
 * If you want to add functionality, do it in one of the calculate-methods.
 * Together they should use all available FlightModifiers to calculate new vector angle and
 * length, and drop rate
 * */
class PlaneLogic(
    private val planeRepository : PlaneRepository,
) : ViewModel() {

    val planeState = planeRepository.planeState
    private val defaultSlowRate = 0.2
    private val planeStartHeight = 100.0
    private val defaultDropRate = 0.1 * planeStartHeight
    private val minPlaneScale = 0.3
    private val maxPlaneScale = 0.6
    private val distanceMultiplier = 1000
    private val groundedThreshold = 0.0
    val updateFrequency: Long = 1000

    private val gainHeightAllowed = false
    private val minDropRate = 1.0

    /**
     * This method fetches the current plane state and uses the position, angle, speed
     * and modifiers to calculate how it should be affected by the weather
     * Wind and plane speeds are in meters(per second). To calculate the distance traveled the speed
     * is multiplied by a constant, distanceMultiplier (probably valued at 1000)
     */
    fun update(weather: Weather){
        // Set up
        val plane = planeRepository.planeState.value

        // Make sure plane doesn't fly if it shouldn't
        if (!plane.flying){
            planeRepository.update(
                plane.copy(
                    speed = 0.0,
                    height = planeStartHeight
                )
            )
            return
        }

        // Calculate the modified trajectory of the plane
        val currentPlaneVector = calculateVector(plane.angle, plane.speed - calculateSpeedLoss(plane.flightModifier, plane.speed) )
        val affectedPlaneVector = calculateNewPlaneVector(currentPlaneVector, weather)

        // Make new plane pos
        val newPlanePos = GeoPoint(plane.pos[0], plane.pos[1]).destinationPoint(
            vectorLength(affectedPlaneVector) * distanceMultiplier,
            calculateAngle(affectedPlaneVector)
        )

        // Calculate new plane stats
        val newPlaneAngle = calculateAngle(affectedPlaneVector)
        val newPlaneSpeed = vectorLength(affectedPlaneVector)
        val newHeight = plane.height - calculateDropRate(weather)
        val flying = newHeight > groundedThreshold

        // Update planeState with the calculated changes
        planeRepository.update(
            plane.copy(
                pos = listOf(newPlanePos.latitude, newPlanePos.longitude),
                speed = newPlaneSpeed,
                height = newHeight,
                angle = newPlaneAngle,
                flying = flying
            )
        )
    }

    /**
     * Calculates the size that the plane composable should be based on min and max values, and what height value of the Plane is.
     * Returns the minPlaneScale if the height of the Plane is 0.0, and maxPlaneScale if the height is equal to the planeStartHeight.
     * Scales linearly.
     */
    fun getPlaneScale(): Float{
        var planeScale = (minPlaneScale + (maxPlaneScale - minPlaneScale) * (planeState.value.height / planeStartHeight)).toFloat()
        if (planeScale < minPlaneScale) planeScale = minPlaneScale.toFloat()
        return planeScale
    }

    /** Calculates a new plane vector based on the available modifiers.
     * The new vector represents the new angle and speed.
     *
     * **Adding functionality:** Functionality that affects plane angle or speed should added here.
     **/
    private fun calculateNewPlaneVector(currentPlaneVector: Vector, weather: Weather): Vector {
        // Adjust for wind-effect
        val windVector = multiplyVector(calculateVector(weather.windAngle, weather.windSpeed), -1.0)
        val affectedWindVector = multiplyVector(windVector, calculateWindEffect())

        return addVectors(currentPlaneVector, affectedWindVector)
    }

    /**
     * Calculates a drop rate in meters that is subtracted in the update-method that is called every
     * step of the plane's flight.
     * Should use available relevant FlightModifiers.
     * Should be a double in the range 0.0 - 1.0 if it should never gain height
     *
     * **Adding functionality:** Any functionality that affects the drop rate goes here.
     */
    fun calculateDropRate(weather: Weather): Double{
        val flightModifier = planeState.value.flightModifier

        val dropRates = listOf(
            {calculateAirPressureDropRate(weather.airPressure, flightModifier)},
            {calculateRainDropRate(weather.rain, flightModifier)},
            {calculateTemperatureDropRate(weather.temperature, flightModifier)}
        )

        val value = 1.0 / dropRates.size
        var newDropRate = 0.0
        for (dropRate: () -> Double in dropRates){
            newDropRate += dropRate() * value
        }

        // if gaining height is not allowed, we cap the drop rate at the minDropRate value and return tht if we would get anything below
        // This is a cheap hack and should be done fundamentally differently by making the modifier effect calculations combined naturally cap out at the minimum
        // now there are many evaluations that result in the minimum possible drop rate, which makes for less distinct choices

        var result = round((flightModifier.weight * defaultDropRate) + (newDropRate * defaultDropRate))
        if (!gainHeightAllowed && result < minDropRate){
            result = minDropRate
        }

        return result
    }

    /**
     * The extremes are based on these values:
     * https://no.wikipedia.org/wiki/Norske_v%C3%A6rrekorder
     */
    fun calculateAirPressureDropRate(airPressure: Double, flightModifier: FlightModifier): Double{
        val airPressureRange = (AIR_PRESSURE_MAX - AIR_PRESSURE_MIN) / 2    // The range of values that the air pressure can change in a positive and negative direction
        val airPressureNormal = AIR_PRESSURE_MIN + airPressureRange

        //val airPressureDropRate = defaultDropRate * (airPressure - airPressureNormal) / airPressureRange
        val airPressureDropRate = (airPressure - airPressureNormal) / airPressureRange

        return airPressureDropRate * -flightModifier.airPressureEffect
    }

    /**
     * The extremes are based on these values:
     * https://no.wikipedia.org/wiki/Norske_v%C3%A6rrekorder
     */
    fun calculateRainDropRate(rain: Double, flightModifier: FlightModifier): Double{
        return rain / RAIN_MAX * flightModifier.rainEffect
        //return rain / RAIN_MAX * defaultDropRate * flightModifier.rainEffect
    }

    fun calculateTemperatureDropRate(temperature: Double, flightModifier: FlightModifier): Double{
        // Consider changing this to use a system of target temperature, range and effect
        val temp = if (temperature.absoluteValue > 0){
            temperature / TEMPERATURE_MAX
        } else {
            temperature / TEMPERATURE_MIN
        }

        //return temp * defaultDropRate * -flightModifier.temperatureEffect
        return temp * -flightModifier.temperatureEffect
    }


    private fun calculateSpeedLoss(flightModifier: FlightModifier, speed: Double): Double{
        // should take plane modifiers into account
        return defaultSlowRate * flightModifier.slowRateEffect * speed
    }

    // Wind
    private fun calculateWindEffect(): Double{
        return planeState.value.flightModifier.windEffect
    }
}