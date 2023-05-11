package com.example.in2000_papirfly.helpers

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

class Vector(
    val x: Double,
    val y: Double,
){
    constructor(values: List<Double>): this(values[0], values[1])

    operator fun get(index: Int): Double{
        if (index == 0){
            return x
        }
        return y
    }
    companion object {

        val zeroDegreeAngle: Vector = Vector(1.0, 0.0)

        fun calculateVector(angle: Double, magnitude: Double): Vector{
            val radianAngle = Math.toRadians(angle)
            val x = magnitude * cos(radianAngle)
            val y = magnitude * cos(Math.toRadians(90.0) - radianAngle)
            return Vector(x, y)
        }

        fun calculateAngle(vector1: Vector, vector2: Vector = zeroDegreeAngle): Double{
            // returns the angle of a vector given in degrees
            var newAngle =  Math.toDegrees( acos(dotProduct(vector1, vector2) / (vectorLength(vector1) * vectorLength(vector2))) )
            if (vector1.y < 0){
                newAngle = 360.0 - newAngle
            }
            return newAngle
        }

        fun dotProduct(vector1: Vector, vector2: Vector): Double{
            return (vector1.x * vector2.y) + (vector1.x * vector2.y)
        }

        fun vectorLength(vector: Vector): Double{
            return sqrt(vector.x.pow(2) + vector.y.pow(2))
        }

        fun addVectors(vector1 : Vector, vector2 : Vector): Vector{
            return Vector(vector1.x + vector2.x, vector1.y + vector2.y)
        }

        fun subtractVectors(vector1: Vector, vector2: Vector): Vector{
            return Vector(vector1.x - vector2.x, vector1.y - vector2.y)
        }

        fun multiplyVector(vector1: Vector, x: Double): Vector{
            return Vector(vector1.x * x, vector1.y * x)
        }
    }
}