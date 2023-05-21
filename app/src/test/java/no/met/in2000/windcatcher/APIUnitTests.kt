package no.met.in2000.windcatcher

import no.met.in2000.windcatcher.network.getLocationforecastData
import no.met.in2000.windcatcher.network.getNowcastData
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