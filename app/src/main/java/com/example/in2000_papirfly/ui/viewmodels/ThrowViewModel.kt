package com.example.in2000_papirfly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.DataRepository
import com.example.in2000_papirfly.data.PlaneRepository
import com.example.in2000_papirfly.data.Weather
import com.example.in2000_papirfly.data.WeatherRepositoryMVP
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class ThrowViewModel(
    var selectedLocation: GeoPoint,
    val mapViewState: DisableMapView,
    val getWeather: (location: String) -> Weather,
//    val weatherRepository: WeatherRepositoryMVP,
    val weatherRepository: DataRepository,
    val planeRepository: PlaneRepository
): ViewModel() {
    // TODO
    // I'm making a lot of new ViewModel objects that should be made somewhere else here
    private val planeLogic = PlaneLogic(planeRepository)
    val planeState = planeLogic.planeState
    val startPos: GeoPoint = selectedLocation
    var previousPlanePos: GeoPoint = selectedLocation
    var nextPlanePos: GeoPoint = selectedLocation
    var weather: Weather = Weather()

    init {
        drawStartMarker(mapViewState, startPos)
        // Get the weather at the start location
        CoroutineScope(Dispatchers.IO).launch {
            weather = weatherRepository.getWeatherAtPoint(selectedLocation)
        }
    }

    fun throwPlane(){
        //  TODO // Calculate or get the angle and speed the plane should be launched at
        val speed = 10.0
        val angle = 0.0
        val planeStartHeight = 100.0

        // initialize starter plane
        planeRepository.update(
            planeState.value.copy(
                speed = speed,
                height = planeStartHeight,
                pos= listOf(selectedLocation.latitude, selectedLocation.longitude),
                flying = true
            )
        )

        previousPlanePos = startPos
        mapViewState.controller.setCenter(startPos)

        // Start the coroutine that updates the plane every second
        viewModelScope.launch{
            //planeLogic.throwPlane(100.0, 98.0, selectedLocation)

            // Locks map
            mapViewState.setInteraction(false)

            while (planeIsFlying()) {
                planeLogic.update(weather)
                nextPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
                // Animate the map
                mapViewState.controller.animateTo(nextPlanePos)

                // Call the weather for the next position
                CoroutineScope(Dispatchers.IO).launch {
                    weather = weatherRepository.getWeatherAtPoint(
                        GeoPoint(
                            planeState.value.pos[0],
                            planeState.value.pos[1]
                        )
                    )
                }
                delay(planeLogic.updateFrequency)
                // TODO // Await the answer for the weather call // Seems to not be needed

                // Draws the plane path
                drawPlanePath(mapViewState, previousPlanePos, nextPlanePos)

                // This fixes the map glitching
                previousPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
            }

            planeLogic.update(weather)
            // Draws goal flag
            drawGoalMarker(mapViewState, startPos, previousPlanePos)

            // Unlock map
            mapViewState.setInteraction(true)
        }
    }




    fun getPlaneScale(): Float{
        // This should not be done here like this I think.
        return planeLogic.getPlaneScale()
    }

    fun planeIsFlying(): Boolean{
        //return planeState.value.height > 0.1
        return planeState.value.flying
    }

    fun getWindAngle(): Double{
//        // Should use selected location
//        return getWeather("Oslo").windAngle
        return 0.0
    }

    fun getWindSpeed(): Double{
        // Should use selected location
//        return getWeather("Oslo").windSpeed
        return 0.0
    }
}