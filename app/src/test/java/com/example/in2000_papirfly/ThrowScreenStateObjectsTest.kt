package com.example.in2000_papirfly

import com.example.in2000_papirfly.data.components.HighScore
import com.example.in2000_papirfly.data.components.ThrowPointList
import com.example.in2000_papirfly.data.components.Weather
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.defaultHighScoreShownMap
import org.junit.Test

import org.junit.Assert.*

class ThrowScreenStateObjectsTest {

    @Test
    fun emptyHighScoreMap_isCorrect() {
        val testMap: MutableMap<String, HighScore> = ThrowScreenUtilities.emptyHighScoreMap()

        assertEquals(ThrowPointList.throwPoints.size, testMap.size)

        ThrowPointList.throwPoints.forEach {
            assertEquals(it.key, testMap[it.key]?.locationName)
            assertEquals(null, testMap[it.key]?.date)
            assertEquals(0, testMap[it.key]?.distance)
            assertEquals(null, testMap[it.key]?.flightPath)
        }
    }

    @Test
    fun emptyThrowPointWeatherList_isCorrect() {
        val testList: List<Weather> = ThrowScreenUtilities.emptyThrowPointWeatherList()

        assertEquals(ThrowPointList.throwPoints.size, testList.size)

        ThrowPointList.throwPoints.toList().forEachIndexed { index, element ->
            assertEquals(0.0, testList[index].windSpeed, 0.0)
            assertEquals(0.0, testList[index].windAngle, 0.0)
            assertEquals(0.0, testList[index].airPressure, 0.0)
            assertEquals(0.0, testList[index].rain, 0.0)
            assertEquals(0.0, testList[index].temperature, 0.0)
            assertEquals("cloudy", testList[index].icon)
            assertEquals(element.first, testList[index].namePos)
        }
    }

    @Test
    fun defaultHighScoreShownMap_isCorrect() {
        val testMap: MutableMap<String, Boolean> = defaultHighScoreShownMap()

        ThrowPointList.throwPoints.forEach {
            assertEquals(false, testMap[it.key])
        }
    }
}