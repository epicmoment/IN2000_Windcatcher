package com.example.in2000_papirfly

import com.example.in2000_papirfly.network.getLocationforecastData
import com.example.in2000_papirfly.network.getNowcastData
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class APIUnitTests {

    @Test
    fun nowCast_isResponding() {
        runBlocking {
            val response = getNowcastData(59.9138, 10.7387)
            Assert.assertEquals("Feature", response.type)
        }
    }

    @Test
    fun locationForecast_isResponding() {
        runBlocking {
            val response = getLocationforecastData(59.9138, 10.7387)
            Assert.assertEquals("Feature", response.type)
        }
    }

}