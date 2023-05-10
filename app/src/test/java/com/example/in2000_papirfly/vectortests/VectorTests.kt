package com.example.in2000_papirfly.vectortests

import com.example.in2000_papirfly.data.PlaneRepository
import com.example.in2000_papirfly.helpers.Vector
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.PlaneLogic
import org.junit.Test


class VectorTests {
    @Test
    fun test_calculateVector_angle_45_magnitude_1(){
        /*
        var plane = Plane()
        val vector45 = Vector(0.7071067811865476,0.7071067811865476)
        val vector = Vector(1.0, 0.0)
        assertEquals(vector45, plane.calculateVector(45.0, 1.0))
        assertEquals(vector45, plane.calculateVector(280.0, 1.0))

         */
    }

    @Test
    fun test_calculateAngle(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = Vector(1.0, 1.0)

        assertEquals(45, planeLogic.calculateAngle(vector1).toInt())
        val vector2 = Vector(-1.0, 1.0)

        assertEquals(135, planeLogic.calculateAngle(vector2).toInt())
        val vector3 = Vector(-1.0, -1.0)

        assertEquals(225, planeLogic.calculateAngle(vector3).toInt())
        val vector4 = Vector(1.0, -1.0)

        assertEquals(315, planeLogic.calculateAngle(vector4).toInt())
    }
    @Test
    fun test_vectorLength(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = Vector(3.0, 4.0)
        assertEquals(5.0, planeLogic.vectorLength(vector1))

        val vector2 = Vector(1.2, 0.0)
        assertEquals(1.2, planeLogic.vectorLength(vector2))
    }

    @Test
    fun test_dotProduct(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = Vector(1.0, 1.0)
        val vector2 = Vector(1.0, 0.0)
        assertEquals(1.0, planeLogic.dotProduct(vector1, vector2))

        val vector3 = Vector(2.0, 2.0)
        val vector4 = Vector(1.0, 3.0)
        assertEquals(8.0, planeLogic.dotProduct(vector3, vector4))
    }

    @Test
    fun test_subtractVectors(){
        val planeLogic = PlaneLogic(PlaneRepository(), WeatherRepository())
        val vector1 = Vector(2.0, 1.0)
        val vector2 = Vector(1.0, 1.0)
        assertEquals(Vector(1.0, 0.0), planeLogic.subtractVectors(vector1, vector2))
    }
}