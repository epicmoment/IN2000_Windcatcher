package com.example.in2000_papirfly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.osmdroid.util.GeoPoint

class ThrowViewModel(
    val locationName: String,
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
    private var _highScore: MutableStateFlow<HighScore> =
        MutableStateFlow(HighScore())
    var highScore: StateFlow<HighScore> = _highScore.asStateFlow()


    init {
        drawStartMarker(mapViewState, startPos)
        // Get the weather at the start location
        CoroutineScope(Dispatchers.IO).launch {
            weather = weatherRepository.getWeatherAtPoint(selectedLocation)
        }
        updateHighScoreState()
    }

    fun throwPlane(){
        //  TODO // Calculate or get the angle and speed the plane should be launched at
        val speed = 10.0
        val angle = 0.0
        val planeStartHeight = 100.0
        val flightPath = mutableListOf<GeoPoint>()
        flightPath.add(startPos)

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
                // Saves flight path point
                flightPath.add(nextPlanePos)

                // This fixes the map glitching
                previousPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
            }

            planeLogic.update(weather)
            val newHS = updateHighScore(startPos, previousPlanePos, flightPath)
            // Draws goal flag
            drawGoalMarker(mapViewState, startPos, previousPlanePos, newHS)


            // Unlock map
            mapViewState.setInteraction(true)
        }
    }

    private fun updateHighScore(startPos: GeoPoint, goalPos: GeoPoint, flightPath: List<GeoPoint>): Boolean {
        val distance = (startPos.distanceToAsDouble(goalPos)/1000).toInt()
        if (highScore.value.distance == null || distance > highScore.value.distance!!) {
            CoroutineScope(Dispatchers.IO).launch {
                weatherRepository.updateHighScore(
                    locationName,
                    distance,
                    System.currentTimeMillis() / 1000L,
                    flightPath
                ) { updateHighScoreState() }
            }
            return true
        }
        return false
    }

    private fun updateHighScoreState() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newHS = weatherRepository.getHighScore(locationName)
                _highScore.update {
                    it.copy(
                        date = newHS.date,
                        distance = newHS.distance,
                        flightPath = newHS.flightPath
                    )
                }
            } catch (e: Throwable) {
                Log.e("Highscore", "Error ${e}")
            }
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