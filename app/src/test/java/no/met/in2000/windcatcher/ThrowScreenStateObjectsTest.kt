package no.met.in2000.windcatcher

import no.met.in2000.windcatcher.data.components.HighScore
import no.met.in2000.windcatcher.data.components.ThrowPointList
import no.met.in2000.windcatcher.data.components.Weather
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.defaultHighScoreShownMap
import org.junit.Test

import org.junit.Assert.*
import org.osmdroid.util.GeoPoint

class ThrowScreenStateObjectsTest {

    @Test
    fun emptyHighScoreMap_isCorrect() {
        val testMap: MutableMap<String, HighScore> = ThrowScreenUtilities.emptyHighScoreMap()

        assertEquals(ThrowPointList.throwPoints.size, testMap.size)

        ThrowPointList.throwPoints.forEach {
            assertEquals(it.key, testMap[it.key]?.locationName)
            assertEquals(0L, testMap[it.key]?.date)
            assertEquals(0, testMap[it.key]?.distance)
            assertEquals(listOf(GeoPoint(0.0,0.0)), testMap[it.key]?.flightPath)
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