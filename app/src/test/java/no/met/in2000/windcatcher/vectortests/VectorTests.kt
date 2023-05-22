package no.met.in2000.windcatcher.vectortests

import no.met.in2000.windcatcher.helpers.Vector
import no.met.in2000.windcatcher.helpers.Vector.Companion.calculateAngle
import no.met.in2000.windcatcher.helpers.Vector.Companion.calculateVector
import no.met.in2000.windcatcher.helpers.Vector.Companion.dotProduct
import no.met.in2000.windcatcher.helpers.Vector.Companion.subtractVectors
import no.met.in2000.windcatcher.helpers.Vector.Companion.vectorLength
import junit.framework.TestCase.assertEquals
import org.junit.Test


class VectorTests {
    @Test
    fun test_calculateVector_angle_45_magnitude_1(){
        val correct = Vector(0.7071067811865476,0.7071067811865476)

        val check01 = calculateVector(45.0, 1.0)
        assertEquals(correct, check01)
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
    fun test_vectorLength_is_null(){
        val vector1 = Vector(0.0, 0.0)
        assertEquals(0.0, vectorLength(vector1))
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
    fun test_subtractVectors_answer_has_positive_values(){
        val vector1 = Vector(2.0, 1.0)
        val vector2 = Vector(1.0, 1.0)

        val correct01 = Vector(1.0, 0.0)
        val check01 = subtractVectors(vector1, vector2)
        assertEquals(correct01, check01)
    }

    @Test
    fun test_subtractVectors_answer_has_negative_values(){
        val vector1 = Vector(-2.0, 1.0)
        val vector2 = Vector(1.0, 2.0)

        val correct = Vector(-3.0, -1.0)
        val check = subtractVectors(vector1, vector2)
        assertEquals("\nvector1 $vector1 vector2 $vector2", correct, check)
    }
}