package com.example.in2000_papirfly

import com.example.in2000_papirfly.network.getNowcast
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class NowcastAPItest {

    @Test
    fun test_API() {
        runBlocking {
            val response = getNowcast(59.9138, 10.7387)
            Assert.assertEquals(response.type, "Point")
        }

    }
}