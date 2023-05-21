package no.met.in2000.windcatcher

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import no.met.in2000.windcatcher.data.database.entities.FlightPathPoint
import no.met.in2000.windcatcher.data.database.entities.ThrowPoint
import no.met.in2000.windcatcher.data.database.entities.WeatherTile
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val database = (appContext.applicationContext as WindcatcherApplication).database
    private val flightPathDao = database.flightPathDao()
    private val tileDao = database.weatherTileDao()
    private val throwDao = database.throwPointDao()

    @Test
    fun testDataBase() {
        val testTile = WeatherTile(
            42.0,
            0.0,
            0,
            0,
            "",
            0.0,
            0.0,
            0.0,
            0.0,
            0.0
        )
        runBlocking {
            tileDao.insert(testTile)
        }

        val testPoint = ThrowPoint("Andeby", 42.0, 0.0, 0, 0)
        runBlocking {
            throwDao.insert(testPoint)
        }

        assertEquals(42.0, throwDao.getThrowPointInfo("Andeby")?.tileX)
    }

    @Test
    fun persistenceTest() {
        assertEquals(42.0, throwDao.getThrowPointInfo("Andeby")?.tileX)
    }

    @Test
    fun fetchEmpty() {

        val result = tileDao.getTileAt(100.0, 100.0)

        assertEquals(null, result)
    }

    @Test
    fun testFlightPath() {
        val testTile = WeatherTile(
            42.0,
            0.0,
            0,
            0,
            "",
            0.0,
            0.0,
            0.0,
            0.0,
            0.0
        )
        runBlocking {
            tileDao.insert(testTile)
        }

        val testPoint = ThrowPoint("Andeby", 42.0, 0.0, 0, 0)
        runBlocking {
            throwDao.insert(testPoint)
        }

        val expected = emptyList<FlightPathPoint>()
        assertEquals(expected, flightPathDao.getFlightPath("Andeby"))


        val testFlight = listOf(
            FlightPathPoint("Andeby", 0, 0.0, 0.0, null),
            FlightPathPoint("Andeby", 1, 1.0, 1.0, null),
            FlightPathPoint("Andeby", 2, 0.0, 0.0, null)
        )

        runBlocking {
            testFlight.forEach {
                flightPathDao.insert(it)
            }
        }
        assertEquals(3, flightPathDao.getSize())
        assertEquals(0.0, flightPathDao.getFlightPath("Andeby")[2].locY)

        flightPathDao.deleteFLightPath("Andeby")

        assertEquals(expected, flightPathDao.getFlightPath("Andeby"))
    }

    @Test
    fun deleteTestDatabase() {
        database.clearAllTables()

        assertEquals(
            0,
            tileDao.getSize() +
                throwDao.getSize() +
                flightPathDao.getSize()
        )
    }
}
