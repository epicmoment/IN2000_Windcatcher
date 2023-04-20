package com.example.in2000_papirfly

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.in2000_papirfly.data.database.entities.ThrowPoint
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.in2000_papirfly", appContext.packageName)
    }

    @Test
    fun testDataBase() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val database = (appContext as PapirflyApplication).db

        val throwDao = database.throwPointDao()
        val testPoint = ThrowPoint("Oslo", 42, 0, null, null)
        runBlocking {
            throwDao.insert(testPoint)
        }

        assertEquals(42, throwDao.getThrowPointInfo("Oslo").tileX)
    }
}