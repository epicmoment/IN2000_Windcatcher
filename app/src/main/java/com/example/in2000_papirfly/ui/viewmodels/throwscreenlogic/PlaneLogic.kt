package com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic

import androidx.lifecycle.ViewModel
import com.example.in2000_papirfly.data.*
import kotlinx.coroutines.*
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
    private val slowRate = 0.8
    private val planeStartHeight = 100.0
    private val defaultDropRate = 0.1 * planeStartHeight
    private val maxPlaneStartSpeed = 20.0
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
    suspend fun update(weather: Weather){
        println("airPressure" + weather.airPressure)

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
        val currentPlaneVector = calculateVector(plane.angle, plane.speed * calculateSlowRate() )
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

    fun planeIsFlying(): Boolean{
        return planeRepository.planeState.value.height > 0.1
    }

    /** Calculates a new plane vector based on the available modifiers.
     * The new vector represents the new angle and speed.
     *
     * **Adding functionality:** Functionality that affects plane angle or speed should added here.
     **/
    private fun calculateNewPlaneVector(currentPlaneVector: List<Double>, weather: Weather): List<Double>{
        // Adjust for wind-effect
        val windVector = multiplyVector(calculateVector(weather.windAngle, weather.windSpeed), -1.0)
        val affectedWindVector = multiplyVector(windVector, calculateWindEffect())
        var newPlaneVector = addVectors(currentPlaneVector, affectedWindVector)

        return newPlaneVector
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
        val airPressure = weather.airPressure
        val rain = weather.rain

        var newDropRate = 0.0
        newDropRate += calculateAirPressureDropRate(airPressure, flightModifier)
        newDropRate += calculateRainDropRate(rain, flightModifier)
        val temperatureDropRate = calculateTemperatureDropRate(weather.temperature, flightModifier)
        println("temperature drop rate: $temperatureDropRate")
        newDropRate += calculateTemperatureDropRate(weather.temperature, flightModifier)
        println("New drop rate: $newDropRate")

        return round(defaultDropRate + newDropRate)
    }

    /**
     * The extremes are based on these values:
     * https://no.wikipedia.org/wiki/Norske_v%C3%A6rrekorder
     */
    fun calculateAirPressureDropRate(airPressure: Double, flightModifier: FlightModifier): Double{
        // Setting up standard values
        val airPressureMin = 937.1    // Lowest air pressure value we usually get
        val airPressureMax = 1061.3   // Highest air pressure value we usually get
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
        val rainMax = 78.5

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


    private fun calculateSlowRate(): Double{
        // should take plane modifiers into account
        return slowRate
    }

    // Wind
    fun calculateWindEffect(): Double{
        return planeState.value.flightModifier.windEffect
    }


    // Help methods

    // This vector stuff should probably be extracted
    fun calculateVector(angle: Double, magnitude: Double): List<Double>{
        val radianAngle = Math.toRadians(angle)
        val x = magnitude * cos(radianAngle)
        val y = magnitude * cos(Math.toRadians(90.0) - radianAngle)
        return listOf(x, y)
    }

    fun calculateAngle(vector1: List<Double>, vector2: List<Double> = zeroDegreeAngle): Double{
        // returns the angle of a vector given in degrees
        var newAngle =  Math.toDegrees( acos(dotProduct(vector1, vector2) / (vectorLength(vector1) * vectorLength(vector2))) )
        if (vector1[1] < 0){
            newAngle = 360.0 - newAngle
        }
        return newAngle
    }

    fun dotProduct(vector1: List<Double>, vector2: List<Double>): Double{
        return (vector1[0] * vector2[0]) + (vector1[1] * vector2[1])
    }

    fun vectorLength(vector: List<Double>): Double{
        return sqrt(vector[0].pow(2) + vector[1].pow(2))
    }

    fun addVectors(vector1 : List<Double>, vector2 : List<Double>): List<Double>{
        return listOf(vector1[0] + vector2[0], vector1[1] + vector2[1])
    }

    fun subtractVectors(vector1: List<Double>, vector2: List<Double>): List<Double>{
        return listOf(vector1[0] - vector2[0], vector1[1] - vector2[1])
    }

    fun multiplyVector(vector1: List<Double>, x: Double): List<Double>{
        return listOf(vector1[0] * x, vector1[1] * x)
    }
}