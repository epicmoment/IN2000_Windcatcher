package com.example.in2000_papirfly

import com.example.in2000_papirfly.Plane.PlaneLogic
import com.example.in2000_papirfly.Plane.PlaneRepository
import com.example.in2000_papirfly.Plane.WeatherRepository
import junit.framework.TestCase.assertEquals
import org.junit.Test

class VectorTests {
    @Test
    fun test_calculateVector_angle_45_magnitude_1(){
        /*
        var plane = Plane()
        val vector45 = listOf(0.7071067811865476,0.7071067811865476)
        val vector = listOf(1.0, 0.0)
        assertEquals(vector45, plane.calculateVector(45.0, 1.0))
        assertEquals(vector45, plane.calculateVector(280.0, 1.0))

         */
    }

    @Test
    fun test_calculateAngle(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = listOf(1.0, 1.0)
        val vector2 = listOf(1.0, 0.0)
        assertEquals(45, planeLogic.calculateAngle(vector1, vector2))
    }
    @Test
    fun test_vectorLength(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = listOf(3.0, 4.0)
        assertEquals(5.0, planeLogic.vectorLength(vector1))

        val vector2 = listOf(1.2, 0.0)
        assertEquals(1.2, planeLogic.vectorLength(vector2))
    }

    @Test
    fun test_dotProduct(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = listOf(1.0, 1.0)
        val vector2 = listOf(1.0, 0.0)
        assertEquals(1.0, planeLogic.dotProduct(vector1, vector2))

        val vector3 = listOf(2.0, 2.0)
        val vector4 = listOf(1.0, 3.0)
        assertEquals(8.0, planeLogic.dotProduct(vector3, vector4))
    }

    @Test
    fun test_subtractVectors(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = listOf(2.0, 1.0)
        val vector2 = listOf(1.0, 1.0)
        assertEquals(listOf(1.0, 0.0), planeLogic.subtractVectors(vector1, vector2))
    }
}