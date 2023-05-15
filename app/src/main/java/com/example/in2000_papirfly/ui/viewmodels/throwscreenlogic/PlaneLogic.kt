package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.example.in2000_papirfly.data.PlaneRepository
import com.example.in2000_papirfly.data.Weather
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.helpers.Vector
import com.example.in2000_papirfly.helpers.Vector.Companion.addVectors
import com.example.in2000_papirfly.helpers.Vector.Companion.calculateAngle
import com.example.in2000_papirfly.helpers.Vector.Companion.calculateVector
import com.example.in2000_papirfly.helpers.Vector.Companion.multiplyVector
import com.example.in2000_papirfly.helpers.Vector.Companion.vectorLength
import org.osmdroid.util.GeoPoint
import kotlin.math.*
/**
 * If you want to add functionality, do it in one of the calculate-methods.
 * Together they should use all available FlightModifiers to calculate new vector angle and
 * length, and drop rate
 * */
class PlaneLogic(
    val planeRepository : PlaneRepository,
    val loadoutRepository: LoadoutRepository,
) : ViewModel() {

    val planeState = planeRepository.planeState
    private val defaultSlowRate = 0.2
    private val planeStartHeight = 100.0
    private val defaultDropRate = 0.1 * planeStartHeight
    private val minPlaneScale = 0.3
    private val maxPlaneScale = 0.6
    val updateFrequency: Long = 1000
    val distanceMultiplier = 1000
    val zeroDegreeAngle = listOf(1.0, 0.0)
    val groundedThreshold = 0.0

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
        val newHeight = plane.height - calculateDropRate(plane.speed, weather)
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
    private fun calculateDropRate(speed: Double, weather: Weather): Double{
        val flightModifier = planeState.value.flightModifier

        var newDropRate = 0.0
        newDropRate += calculateAirPressureDropRate(weather.airPressure, flightModifier)
        newDropRate += calculateRainDropRate(weather.rain, flightModifier)
        newDropRate += calculateTemperatureDropRate(weather.temperature, flightModifier)

        return round((flightModifier.weight * defaultDropRate) + newDropRate)
    }

    /**
     * The extremes are based on these values:
     * https://no.wikipedia.org/wiki/Norske_v%C3%A6rrekorder
     */
    fun calculateAirPressureDropRate(airPressure: Double, flightModifier: FlightModifier): Double{
        // Setting up standard values
        //val airPressureMin = 937.1    // Lowest air pressure value we usually get
        //val airPressureMax = 1061.3   // Highest air pressure value we usually get
        val airPressureMin = 983.0
        val airPressureMax = 1033.0
        val airPressureRange = (airPressureMax - airPressureMin) / 2    // The range of values that the air pressure can change in a positive and negative direction
        val airPressureNormal = airPressureMin + airPressureRange

        val airPressureDropRate = defaultDropRate * (airPressure - airPressureNormal) / airPressureRange

        return airPressureDropRate * -flightModifier.airPressureEffect
    }

    /**
     * The extremes are based on these values:
     * https://no.wikipedia.org/wiki/Norske_v%C3%A6rrekorder
     */
    fun calculateRainDropRate(rain: Double, flightModifier: FlightModifier): Double{
        //val rainMax = 78.5
        val rainMax = 10.0

        return rain / rainMax * defaultDropRate * flightModifier.rainEffect
    }

    fun calculateTemperatureDropRate(temperature: Double, flightModifier: FlightModifier): Double{
        // Consider changing this to use a system of target temperature, range and effect
        val temperatureMax = 35.6
        val temperatureMin = -51.4

        val temp = if (temperature.absoluteValue > 0){
            temperature / temperatureMax
        } else{
            temperature / temperatureMin
        }

        return temp * defaultDropRate * -flightModifier.temperatureEffect
    }


    private fun calculateSpeedLoss(flightModifier: FlightModifier, speed: Double): Double{
        // should take plane modifiers into account
        return defaultSlowRate * flightModifier.slowRateEffect * speed
    }

    // Wind
    fun calculateWindEffect(): Double{
        return planeState.value.flightModifier.windEffect
    }
}