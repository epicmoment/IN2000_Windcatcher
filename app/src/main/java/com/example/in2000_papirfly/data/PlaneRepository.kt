package com.example.in2000_papirfly.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlaneRepository(){

    private val _planeState = MutableStateFlow(Plane())
    val planeState : StateFlow<Plane> = _planeState.asStateFlow()

    fun update(newPlane : Plane){
        _planeState.update { _ -> newPlane }

        /*
        val plane = if (newPlane.pos[0] == Double.NaN || newPlane.pos[1] == Double.NaN){
           newPlane.copy(pos = listOf(0.0, 0.0))
        } else {
            newPlane
        }
        _planeState.update { _ -> plane }

         */
    }

    fun getCurrentPlane(): Plane{
        return planeState.value
    }
}