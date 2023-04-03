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
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.drawGoalMarker
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.drawPlanePath
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class ThrowViewModel(
    var selectedLocation: GeoPoint,
    val mapViewState: MapView,
    val getWeather: (location: String) -> Weather,
    val weatherRepository: WeatherRepositoryMVP,
    val planeRepository: PlaneRepository
): ViewModel() {
    // TODO
    // I'm making a lot of new ViewModel objects that should be made somewhere else here
    private val planeLogic = PlaneLogic(planeRepository)
    val planeState = planeLogic.planeState
    var previousPlanePos: GeoPoint = selectedLocation
    var nextPlanePos: GeoPoint = selectedLocation

    fun throwPlane(){
        //  TODO // Calculate or get the angle and speed the plane should be launched at
        val speed = 10.0
        val angle = 0.0
        val planeStartHeight = 100.0

        // initialize starter plane
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

            // Get the weather at the start location
            var weather = weatherRepository.getWeatherAtPoint(selectedLocation.latitude, selectedLocation.longitude)

            while (planeIsFlying()) {
                planeLogic.update(weather)
                nextPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
                // Animate the map
                mapViewState.controller.animateTo(nextPlanePos)

                // Call the weather for the next position
                weather = weatherRepository.getWeatherAtPoint(planeState.value.pos[0], planeState.value.pos[1])
                delay(planeLogic.updateFrequency)
                // TODO // Await the answer for the weather call // Seems to not be needed

                // Draws the plane path
                drawPlanePath(mapViewState, previousPlanePos, nextPlanePos)

                // This fixes the map glitching
                previousPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
            }

            // Draws goal flag
            drawGoalMarker(mapViewState, previousPlanePos)
        }
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