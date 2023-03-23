package com.example.in2000_papirfly.data

import java.util.Vector
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

val slowRate = 0.1

data class Plane(
    var pos : List<Double> = listOf(59.943325914913615, 10.717908529673489),
    var launched : Boolean = false,
    var angle : Double = 0.0,
    var speed : Double = 0.0,
    var height : Double = 100.0
){
    fun update(wind : Wind){
        // Slow plane down every time
        val newSpeed = speed * slowRate

        val planeVector = calculateVector(angle, newSpeed)
        //val windVector =

        // Make new plane pos
        val newPlanePos = addVectors(pos, planeVector)

        // Temp in case we should make a PlaneRepository
        pos = newPlanePos
    }

    fun calculateVector(angle: Double, magnitude: Double): List<Double>{
        val radianAngle = Math.toRadians(angle)
        val x = magnitude * cos(radianAngle)
        val y = magnitude * cos(Math.toRadians(90.0) - radianAngle)
        return listOf(x, y)
    }

    fun addVectors(vector1 : List<Double>, vector2 : List<Double>): List<Double>{
        return listOf(vector1[0] + vector2[0], vector1[1] + vector2[1])
    }
}
