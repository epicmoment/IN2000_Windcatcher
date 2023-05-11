package com.example.in2000_papirfly.vectortests

import com.example.in2000_papirfly.helpers.Vector
import com.example.in2000_papirfly.helpers.Vector.Companion.calculateAngle
import com.example.in2000_papirfly.helpers.Vector.Companion.calculateVector
import com.example.in2000_papirfly.helpers.Vector.Companion.dotProduct
import com.example.in2000_papirfly.helpers.Vector.Companion.subtractVectors
import com.example.in2000_papirfly.helpers.Vector.Companion.vectorLength
import junit.framework.TestCase.assertEquals
import org.junit.Test


class VectorTests {
    @Test
    fun test_calculateVector_angle_45_magnitude_1(){
        val vector45 = Vector(0.7071067811865476,0.7071067811865476)

        val check01 = calculateVector(45.0, 1.0)
        assertEquals(vector45.x, check01.x)
        assertEquals(vector45.y, check01.y)
    }

    @Test
    fun test_calculateAngle(){
        val vector1 = Vector(1.0, 1.0)
        assertEquals(45, calculateAngle(vector1).toInt())

        val vector2 = Vector(-1.0, 1.0)
        assertEquals(135, calculateAngle(vector2).toInt())

        val vector3 = Vector(-1.0, -1.0)
        assertEquals(225, calculateAngle(vector3).toInt())

        val vector4 = Vector(1.0, -1.0)
        assertEquals(315, calculateAngle(vector4).toInt())
    }
    @Test
    fun test_vectorLength(){
        val vector1 = Vector(3.0, 4.0)
        assertEquals(5.0, vectorLength(vector1))

        val vector2 = Vector(1.2, 0.0)
        assertEquals(1.2, vectorLength(vector2))
    }

    @Test
    fun test_dotProduct(){
        val vector1 = Vector(1.0, 1.0)
        val vector2 = Vector(1.0, 0.0)
        assertEquals(1.0, dotProduct(vector1, vector2))

        val vector3 = Vector(2.0, 2.0)
        val vector4 = Vector(1.0, 3.0)
        assertEquals(8.0, dotProduct(vector3, vector4))
    }

    @Test
    fun test_subtractVectors(){
        val vector1 = Vector(2.0, 1.0)
        val vector2 = Vector(1.0, 1.0)
        assertEquals(Vector(1.0, 0.0), subtractVectors(vector1, vector2))
    }
}