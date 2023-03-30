package com.example.in2000_papirfly.Plane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val maxPlaneStartSpeed = 100.0
    private val minPlaneScale = 0.3
    private val maxPlaneScale = 0.6
    private val updateFrequency: Long = 1000

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



        val planeVector = calculateVector(plane.angle, newSpeed)
        val windVector = calculateVector(wind.angle, wind.speed)

        // Affect the trajectory of the plane
        val affectedPlaneVector = addVectors(planeVector, multiplyVector(windVector, plane.flightModifier.windEffect))

        // Make new plane pos
        /*
        val newPlanePos = calculateDestinationPoint(
            distance = vectorLength(affectedPlaneVector),
            angle = calculateAngle(affectedPlaneVector),
            plane.pos
        )
        */

        val newPlanePos = addVectors(plane.pos, affectedPlaneVector)

        // Calculate new plane angle
        val newPlaneAngle = calculateAngle(subtractVectors(newPlanePos, plane.pos), listOf(1.0, 0.0))


        // Update planeState
        planeRepository.update(plane.copy(pos = newPlanePos, speed = newSpeed, height = newHeight, angle = newPlaneAngle))
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
        return dropRate * getPlaneSpeed() / maxPlaneStartSpeed
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


    fun calculateDestinationPoint(distance: Double, angle: Double, currentPos: List<Double>): List<Double>{
        // This method is going to be replaced by the one Sivert has
        val coordinateDistance = distance / 110.0
        return addVectors(currentPos, calculateVector(coordinateDistance, angle))
    }

    fun calculateVector(angle: Double, magnitude: Double): List<Double>{
        val radianAngle = Math.toRadians(angle)
        val x = magnitude * cos(radianAngle)
        val y = magnitude * cos(Math.toRadians(90.0) - radianAngle)
        return listOf(x, y)
    }

    fun calculateAngle(vector1: List<Double>, vector2: List<Double> = listOf(1.0, 0.0)): Double{
        // returns the angle of a vector given in degrees
        return Math.toDegrees( acos(dotProduct(vector1, vector2) / (vectorLength(vector1) * vectorLength(vector2))) )
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