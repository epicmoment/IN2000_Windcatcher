package com.example.in2000_papirfly;

import android.content.Context
import android.database.sqlite.SQLiteDatabase.deleteDatabase
import android.util.Log
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.in2000_papirfly.data.database.PapirflyDatabase;
import com.example.in2000_papirfly.data.database.entities.FlightPathPoint
import com.example.in2000_papirfly.data.database.entities.ThrowPoint
import com.example.in2000_papirfly.data.database.entities.WeatherTile
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testDatabaseName = "test-database"
    private val database = Room.databaseBuilder(
        appContext,
        PapirflyDatabase::class.java,
        testDatabaseName
    ).build()
    private val flightPathDao = database.flightPathDao()
    private val tileDao = database.weatherTileDao()
    private val throwDao = database.throwPointDao()


    @Test
    fun testDataBase() {
        val testTile = WeatherTile(
            42,
            0,
            0,
            "",
            0.0,
            0.0,
            0.0,
            0.0
        )
        runBlocking {
            tileDao.insert(testTile)
        }

        val testPoint = ThrowPoint("Andeby", 42, 0, null, null)
        runBlocking {
            throwDao.insert(testPoint)
        }

        assertEquals(42, throwDao.getThrowPointInfo("Andeby").tileX)
    }

    @Test
    fun persistenceTest() {
        assertEquals(42, throwDao.getThrowPointInfo("Andeby").tileX)
    }

    @Test
    fun testFlightPath() {
        val testPoint = ThrowPoint("Andeby", 42, 0, null, null)
        runBlocking {
            throwDao.insert(testPoint)
        }

        val expected = emptyList<FlightPathPoint>()
        assertEquals(expected, flightPathDao.getFlightPath("Andeby"))


        val testFlight = listOf(
            FlightPathPoint("Andeby", 0, 0, 0, null),
            FlightPathPoint("Andeby", 1, 1, 1, null),
            FlightPathPoint("Andeby", 2, 0, 0, null)
        )

        runBlocking {
            flightPathDao.insert(testFlight)
        }
        assertEquals(3, flightPathDao.getSize())
        assertEquals(0, flightPathDao.getFlightPath("Andeby")[2].locY)

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
