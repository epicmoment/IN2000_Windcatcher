package com.example.in2000_papirfly.Plane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.calculateDestinationPoint
import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import kotlin.math.*

class PlaneLogic(
    private val planeRepository : PlaneRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val planeState = planeRepository.planeState
    private val dropRate = 0.8
    private val slowRate = 0.8
    private val planeStartHeight = 100.0
    private val maxPlaneStartSpeed = 10.0
    private val minPlaneScale = 0.3
    private val maxPlaneScale = 0.6
    public val updateFrequency: Long = 1000

    fun update(){
        // Set up
        val wind = weatherRepository.windState.value
        val plane = planeRepository.planeState.value

        // Calculate the effect of the weather based on
        // weather data and plane modifiers
        // Slow plane down every time
        val newSpeed = plane.speed * calculateSlowRate()

        // Decrease height based on the modifier
        val newHeight = plane.height * calculateDropRate()



        val planeVector = calculateVector(plane.angle, newSpeed / 110)
        val windVector = calculateVector(wind.angle, wind.speed / 110)

        // Affect the trajectory of the plane
        val affectedPlaneVector = addVectors(planeVector, multiplyVector(windVector, plane.flightModifier.windEffect))

        // Make new plane pos
        /*
        val newPlanePos = calculateDestinationPoint(
            distance = vectorLength(affectedPlaneVector),
            direction = calculateAngle(affectedPlaneVector),
            currentPosition = GeoPoint(plane.pos[0], plane.pos[1])
        )
        */

        val newVector = addVectors(plane.pos, affectedPlaneVector)
        val newPlanePos = GeoPoint(newVector[0], newVector[1])

        // Calculate new plane angle
        val newPlaneAngle = calculateAngle(subtractVectors(listOf(newPlanePos.latitude, newPlanePos.longitude), plane.pos), listOf(1.0, 0.0))


        // Update planeState
        planeRepository.update(plane.copy(pos = listOf(newPlanePos.latitude, newPlanePos.longitude), speed = newSpeed, height = newHeight, angle = newPlaneAngle))
    }

    suspend fun throwPlane(speed: Double, angle: Double, startPos: GeoPoint){
        // initialize plane
        planeRepository.update(Plane(speed = speed, angle = angle, height = planeStartHeight, pos= listOf(startPos.latitude, startPos.longitude)))

        while (planeIsFlying()) {
            weatherRepository.updateWindState()
            update()
            delay(updateFrequency)
        }

    }



    fun getPlanePos(): List<Double>{
        return planeRepository.planeState.value.pos
    }

    fun getPlaneAngle(): Double{
        return planeRepository.planeState.value.angle
    }

    fun getPlaneHeight(): Double{
        return planeRepository.planeState.value.height
    }

    fun getPlaneSpeed(): Double{
        return planeRepository.planeState.value.speed
    }

    fun getPlaneScale(): Float{
        return (minPlaneScale + (maxPlaneScale - minPlaneScale) * (getPlaneHeight() / planeStartHeight)).toFloat()
    }

    fun planeIsFlying(): Boolean{
        return planeRepository.planeState.value.height > 0.1
    }

    private fun calculateDropRate(): Double{
        // Should calculate how much or little the plane height should decrease
        // Should be a double in the range 0.0 - 1.0 if it should never gain height

        // TEMP //
        // Currently only affected by plane speed
        // if speed is low drop rate is high
        return dropRate //* getPlaneSpeed() / maxPlaneStartSpeed / 110
    }

    private fun calculateSlowRate(): Double{
        // should take plane modifiers into account
        return slowRate
    }

    // Wind
    fun getWindAngle(): Double{
        return weatherRepository.windState.value.angle
    }


    // Help methods


    fun calculateDestinationPoint(currentPosition: GeoPoint, distance: Double, direction: Double): GeoPoint{
        // This method is going to be replaced by the one Sivert has
        val coordinateDistance = distance / 110.0
        val newVector = addVectors(listOf(currentPosition.latitude, currentPosition.longitude), calculateVector(coordinateDistance, direction))
        return GeoPoint(newVector[0], newVector[1])
    }



    /* ChatGPT wrote the following function based on the following prompt:
 * Hi! Can you write me a Kotlin function that takes the following inputs:
 * distance (in kilometers), direction (in degrees), and coordinates (as latitude and longitude),
 * and returns the new point (with latitude and longitude) you would end up at if you went the
 * given direction for the given distance?
 *//*
    fun calculateDestinationPoint(currentPosition: GeoPoint, distance: Double, direction: Double): GeoPoint {
        // Julian addition
        val correctedDirection = direction - 0.0

        val R = 6371.0 // Earth's radius in km
        val lat1 = currentPosition.latitude * PI / 180.0 // Convert latitude to radians
        val lon1 = currentPosition.longitude * PI / 180.0 // Convert longitude to radians
        val brng = correctedDirection * PI / 180.0 // Convert bearing to radians
        val d = distance / R // Convert distance to angular distance in radians

        val lat2 = asin(sin(lat1) * cos(d) + cos(lat1) * sin(d) * cos(brng))
        val lon2 = lon1 + atan2(sin(brng) * sin(d) * cos(lat1), cos(d) - sin(lat1) * sin(lat2))

        return GeoPoint(lat2 * 180.0 / PI, lon2 * 180.0 / PI) // Convert back to degrees
    }
    */


    fun calculateVector(angle: Double, magnitude: Double): List<Double>{
        val radianAngle = Math.toRadians(angle)
        val x = magnitude * cos(radianAngle)
        val y = magnitude * cos(Math.toRadians(90.0) - radianAngle)
        return listOf(x, y)
    }

    fun calculateAngle(vector1: List<Double>, vector2: List<Double> = listOf(1.0, 0.0)): Double{
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