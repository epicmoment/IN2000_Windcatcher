package com.example.in2000_papirfly.Plane

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class WeatherRepository {
    private val _windState = mutableStateOf(Wind(10.0, 10.0))
    val windState : State<Wind> = _windState

    fun updateWindState(){
        _windState.value = Wind(Math.random()*10, Math.random()*360)
    }
}