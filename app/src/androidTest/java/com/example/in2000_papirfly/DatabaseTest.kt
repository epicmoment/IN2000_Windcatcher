package com.example.in2000_papirfly;

import android.content.Context
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.in2000_papirfly.data.database.PapirflyDatabase;
import com.example.in2000_papirfly.data.database.entities.ThrowPoint
import com.example.in2000_papirfly.data.database.entities.WeatherTile
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val database = Room.databaseBuilder(
        appContext,
        PapirflyDatabase::class.java, "papirfly-database"
    ).build()

    @Test
    fun testDataBase() {

        val tileDao = database.weatherTileDao()
        val testTile = WeatherTile(42, 0, 0, "", 0.0, 0.0, 0.0, 0.0)
        runBlocking {
            tileDao.insert(testTile)
        }

        val throwDao = database.throwPointDao()
        val testPoint = ThrowPoint("Oslo", 42, 0, null, null)
        runBlocking {
            throwDao.insert(testPoint)
        }

        assertEquals(42, throwDao.getThrowPointInfo("Oslo").tileX)
    }

    @Test
    fun persistenceTest() {
        val throwDao = database.throwPointDao()
        assertEquals(42, throwDao.getThrowPointInfo("Oslo").tileX)
    }
}
