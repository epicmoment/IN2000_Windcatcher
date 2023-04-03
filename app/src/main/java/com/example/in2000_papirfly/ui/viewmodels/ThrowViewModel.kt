package com.example.in2000_papirfly.ui.viewmodels

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.Plane
import com.example.in2000_papirfly.data.PlaneRepository
import com.example.in2000_papirfly.data.Weather
import com.example.in2000_papirfly.data.WeatherRepositoryMVP
import com.example.in2000_papirfly.plane.WeatherRepository
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.PlaneLogic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class ThrowViewModel(
    var selectedLocation: GeoPoint,
    val mapViewState: MapView,
    val getWeather: (location: String) -> Weather,
    val planeRepository: PlaneRepository
): ViewModel() {
    // TODO
    // I'm making a lot of new ViewModel objects that should be made somewhere else here
    //val planeRepository = PlaneRepository()
    private val planeLogic = PlaneLogic(planeRepository, getWeather)
    val planeState = planeLogic.planeState
    var previousPlanePos: GeoPoint = selectedLocation
    var nextPlanePos: GeoPoint = selectedLocation

    fun throwPlane(){
        // Calculate or get the angle and speed the plane should be launched at
        // TODO //
        val speed = 10.0
        val angle = 0.0
        val planeStartHeight = 100.0
        // initialize plane
        planeRepository.update(
            Plane(
                speed = speed,
                angle = angle,
                height = planeStartHeight,
                pos= listOf(selectedLocation.latitude, selectedLocation.longitude)
            )
        )

        // Start the coroutine that updates the plane every second
        viewModelScope.launch{
            //planeLogic.throwPlane(100.0, 98.0, selectedLocation)

            while (planeIsFlying()) {
                planeLogic.update()
                // Animate the map
                mapViewState.controller.animateTo(GeoPoint(planeState.value.pos[0], planeState.value.pos[1]))

                delay(planeLogic.updateFrequency)
                // This fixes the map glitching
                previousPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
            }
        }
    }

    // Lifting out fetch methods
    fun getPlanePos(): List<Double>{
        return planeState.value.pos
    }

    suspend fun getPlaneAngle(): Double{
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
        // Should use selected location
        return getWeather("Oslo").windAngle
    }

    fun getWindSpeed(): Double{
        // Should use selected location
        return getWeather("Oslo").windSpeed
    }
}