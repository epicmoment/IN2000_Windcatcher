package com.example.in2000_papirfly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import org.osmdroid.api.IMapController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

class ThrowViewModel(
    var locationName: String,
    var selectedLocation: GeoPoint,
    val markerFactory: (type: String) -> Marker,
    val mapOverlay: MutableList<Overlay>,
    val mapController: IMapController,
    updateOnMoveMap: (() -> Unit) -> Unit,
    val setInteraction: (Boolean) -> Unit,
    val weatherRepository: DataRepository,
    val planeRepository: PlaneRepository
): ViewModel() {
    private val planeLogic = PlaneLogic(planeRepository)
    val planeState = planeLogic.planeState

    var planeFlying: Job = Job()
    var flyingState = false

    private var startPos: GeoPoint = selectedLocation
    var previousPlanePos: GeoPoint = selectedLocation
    var nextPlanePos: GeoPoint = selectedLocation

    var weather: Weather = Weather()
    private val throwScreenState = MutableStateFlow<ThrowScreenState>(
        ThrowScreenState.Throwing)

    // Fetches and caches weather for all throw points
    private var _throwWeatherState: MutableStateFlow<ThrowPointWeatherState> =
        MutableStateFlow(ThrowPointWeatherState(emptyThrowPointWeatherList()))
    var throwWeatherState: StateFlow<ThrowPointWeatherState> = _throwWeatherState.asStateFlow()

    // Fetches and caches all high scores found in database
    private var _throwPointHighScores: MutableStateFlow<MutableMap<String, HighScore>> =
        MutableStateFlow(emptyHighScoreMap())
    val throwPointHighScores: StateFlow<MutableMap<String, HighScore>> = _throwPointHighScores.asStateFlow()

    // Caches if a specific high score is shown on the map or not
    private val _highScoresOnMapState: MutableStateFlow<MutableMap<String, Boolean>> =
        MutableStateFlow(getHighScoreShownStates())
    val highScoresOnMapState = _highScoresOnMapState.asStateFlow()

    val allMarkers = emptyList<Marker>().toMutableList()

    init {
        updateOnMoveMap{ throwScreenState.update{ ThrowScreenState.MovingMap } }
        mapController.setZoom(12.0)

        // Get the weather at the start location
        // TODO this could be fetched from throwWeatherState if we populate it before 'weather'
        CoroutineScope(Dispatchers.IO).launch {
            weather = weatherRepository.getWeatherAtPoint(selectedLocation)
        }

        // Clears all overlays from the map, and then draws every cached flight path
        mapOverlay.clear()
        FlightPathRepository.flightPaths.forEach { path ->
            var previous: GeoPoint? = null
            path.second.forEach { point ->
                if (previous != null) {
                    drawPlanePath(mapOverlay, previous!!, point)
                }
                previous = point
            }
            allMarkers.add(drawGoalMarker(markerFactory, mapOverlay, path.second[0], path.second[path.second.lastIndex], false))
        }
        // Places every throw point
        ThrowPointList.throwPoints.forEach {
            allMarkers.add(drawStartMarker(markerFactory, { updateThrowPointWeather() }, { moveLocation(it.value, it.key) }, mapOverlay, it.value, it.key))
        }
        updateHighScores()
        updateThrowPointWeather()
    }

    private fun redrawMapMarkers() {
        for (marker in allMarkers) {
            mapOverlay.remove(marker)
            mapOverlay.add(marker)
        }
    }

    /**
     * This method produces a map where the keys are the names of the throw point locations,
     * and the values are all empty 'HighScore'-objects.
     *
     * @return The mutable map as described above
     */
    private fun emptyHighScoreMap(): MutableMap<String, HighScore> {
        val map = emptyMap<String, HighScore>().toMutableMap()
        ThrowPointList.throwPoints.forEach {
            map[it.key] = HighScore(locationName = it.key)
        }
        return map
    }

    private fun emptyThrowPointWeatherList(): List<Weather> {
        val weather = emptyList<Weather>().toMutableList()
        ThrowPointList.throwPoints.forEach {
            weather.add(
                Weather(
                    namePos = it.key
                )
            )
        }
        return weather
    }

    fun moveLocation(newLocation: GeoPoint, newLocationName: String) {
        selectedLocation = newLocation
        locationName = newLocationName
        startPos = selectedLocation
        previousPlanePos = selectedLocation
        nextPlanePos = selectedLocation
        CoroutineScope(Dispatchers.IO).launch {
            weather = weatherRepository.getWeatherAtPoint(selectedLocation)
        }
        updateHighScores()
    }

    fun getThrowScreenState(): StateFlow<ThrowScreenState>{
        return throwScreenState.asStateFlow()
    }

    fun throwPlane(){
        //  TODO // Calculate or get the angle and speed the plane should be launched at
        val speed = 10.0
        val angle = 0.0
        val planeStartHeight = 100.0
        val flightPath = mutableListOf<GeoPoint>()
        flightPath.add(startPos)

        throwScreenState.update{ ThrowScreenState.Flying }

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
        mapController.setCenter(startPos)

        // Start the coroutine that updates the plane every second
        planeFlying = viewModelScope.launch {
            flyingState = true
            // Locks map
            setInteraction(false)

            while (planeIsFlying()) {
                planeLogic.update(weather)
                nextPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
                // Animate the map
                mapController.animateTo(nextPlanePos)

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
                drawPlanePath(mapOverlay, previousPlanePos, nextPlanePos)
                // Saves flight path point
                flightPath.add(nextPlanePos)

                // This fixes the map glitching
                previousPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
            }

            planeLogic.update(weather)
            val distance = (startPos.distanceToAsDouble(previousPlanePos)/1000).toInt()
            val newHS = updateHighScore(distance, flightPath)
            // Draws goal flag
            allMarkers.add(drawGoalMarker(markerFactory, mapOverlay, startPos, previousPlanePos, newHS))
            // Shifts all markers in front of flight paths
            redrawMapMarkers()

            FlightPathRepository.flightPaths.add(Pair(distance, flightPath))

            flyingState = false
            // Unlock map
            setInteraction(true)

            throwScreenState.update{ ThrowScreenState.MovingMap }
        }
    }

    private fun updateHighScore(
        distance: Int,
        flightPath: List<GeoPoint>
    ): Boolean {
        if (distance > throwPointHighScores.value[locationName]!!.distance) {
                weatherRepository.updateHighScore(
                    locationName,
                    distance,
                    System.currentTimeMillis() / 1000L,
                    flightPath
                ) { updateHighScores() }
            return true
        }
        return false
    }

    fun updateHighScoreShownState(location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val copy = mutableMapOf<String, Boolean>()
            highScoresOnMapState.value.toMap(copy)
            copy[location] = !copy[location]!!
            Log.d("HighScores", "Updated shown value to ${copy[location]}")
            _highScoresOnMapState.update {
                copy
            }
        }
    }

    /**
     * Initializes a map that keeps track of whether the high score is shown for a spesific point
     */
    private fun getHighScoreShownStates(): MutableMap<String, Boolean> {
        val highScoreShownStates = emptyMap<String, Boolean>().toMutableMap()
        ThrowPointList.throwPoints.forEach {
            highScoreShownStates[it.key] = false
        }
        return highScoreShownStates
    }

    private fun updateHighScores() {
        CoroutineScope(Dispatchers.IO).launch {
            _throwPointHighScores.update {
                it.forEach { (k, v) ->
                    it[k] = weatherRepository.getHighScore(k)
                }
                return@update it
            }
        }
    }

    private fun updateThrowPointWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            val newWeather = weatherRepository.getThrowPointWeatherList()
            _throwWeatherState.update {
                return@update ThrowPointWeatherState(newWeather)
            }
        }
    }

    fun resetPlane(){
        planeRepository.update(Plane())
    }

    fun changeAngle(value: Float){
        throwScreenState.update{ThrowScreenState.Throwing}
        val plane = planeState.value
        planeRepository.update(plane.copy(angle = value.toDouble() * 360))
        previousPlanePos = startPos
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

class ThrowViewModelFactory(
    val weatherRepository: DataRepository,
    val planeRepository: PlaneRepository
){
    fun newViewModel(
        locationName: String,
        selectedLocation: GeoPoint,
        mapViewState: DisableMapView,
        openBottomSheet: (Int) -> Unit,

    ): ThrowViewModel{
        return ThrowViewModel(
            locationName = locationName,
            selectedLocation = selectedLocation,
            markerFactory = { type ->
                if (type == "Start") {
                    return@ThrowViewModel ThrowPositionMarker(
                        mapViewState,
                        openBottomSheet
                    )
                } else {
                    return@ThrowViewModel Marker(mapViewState)
                }
            },
            mapOverlay = mapViewState.overlays,
            mapController = mapViewState.controller,
            updateOnMoveMap = { inputUpdate: () -> Unit
                -> mapViewState.updateOnMoveMap {
                    inputUpdate()
                }
            },
            setInteraction = { interactionEnabled: Boolean
                -> mapViewState.setInteraction(interactionEnabled)
            },
            planeRepository = planeRepository,
            weatherRepository = weatherRepository
        )
    }
}

data class ThrowPointWeatherState(
    val weather: List<Weather>
)

sealed interface ThrowScreenState{

    object Flying: ThrowScreenState

    object Throwing: ThrowScreenState

    object MovingMap: ThrowScreenState
}