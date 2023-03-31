package com.example.in2000_papirfly.plane

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class WeatherRepository {
    private val _windState = mutableStateOf(Wind(10.0, 10.0))
    val windState : State<Wind> = _windState

    fun updateWindState(){
        var plusMinus = { random: Double -> if (random < 0.5)  -1 else 1 }
        val newAngle = windState.value.angle + Math.random()*90 * plusMinus(Math.random())
        _windState.value = Wind(Math.random()*10, newAngle)
    }
}