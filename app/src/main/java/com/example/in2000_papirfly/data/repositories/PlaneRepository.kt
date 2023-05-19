package com.example.in2000_papirfly.data.repositories

import com.example.in2000_papirfly.data.components.Plane
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


interface PlaneRepository{
    val planeState: StateFlow<Plane>
    fun update(newPlane: Plane)
}
class PlaneRepo(): PlaneRepository {

    private val _planeState = MutableStateFlow(Plane())
    override val planeState : StateFlow<Plane> = _planeState.asStateFlow()

    override fun update(newPlane : Plane){
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

    fun getCurrentPlane(): Plane {
        return planeState.value
    }
}