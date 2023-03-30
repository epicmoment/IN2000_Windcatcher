package com.example.in2000_papirfly.Plane

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlaneRepository(){

    private val _planeState = MutableStateFlow(Plane())
    val planeState : StateFlow<Plane> = _planeState.asStateFlow()

    /*
    private val _planeState = mutableStateOf(Plane())
    val planeState : State<Plane> = _planeState
    */
    fun update(newPlane : Plane){
        //_planeState.value = newPlane
        _planeState.update { _ -> newPlane }
    }
}