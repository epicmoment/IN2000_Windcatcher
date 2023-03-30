package com.example.in2000_papirfly.Plane

import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.Location
import kotlinx.coroutines.launch

class ThrowViewModel: ViewModel() {
    // TODO
    // I'm making a lot of new ViewModel objects that should be made somewhere else here
    val planeRepository = PlaneRepository()
    val weatherRepository = WeatherRepository()
    private val planeLogic = PlaneLogic(planeRepository, weatherRepository)
    val planeState = planeLogic.planeState

    fun throwPlane(selectedLocation: Location){
        viewModelScope.launch{
            planeLogic.throwPlane(100.0, 98.0)

        }
    }


    // Lifting out fetch methods
    fun getPlanePos(): List<Double>{
        return planeState.value.pos
    }

    fun getPlaneAngle(): Double{
        return planeState.value.angle
    }

    fun getPlaneHeight(): Double{
        return planeState.value.height
    }

    fun getPlaneSpeed(): Double{
        return planeState.value.speed
    }

    fun getPlaneScale(): Float{
        // This should not be done here like this I think.
        return planeLogic.getPlaneScale()
    }

    fun planeIsFlying(): Boolean{
        return planeState.value.height > 0.1
    }

    fun getWindAngle(): Double{
        return weatherRepository.windState.value.angle
    }
}