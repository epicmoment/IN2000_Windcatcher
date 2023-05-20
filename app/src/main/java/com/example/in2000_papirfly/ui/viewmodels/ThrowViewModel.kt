package com.example.in2000_papirfly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.components.HighScore
import com.example.in2000_papirfly.data.components.Plane
import com.example.in2000_papirfly.data.components.ThrowPointList
import com.example.in2000_papirfly.data.components.Weather
import com.example.in2000_papirfly.data.database.DataBaseContentNegotiator
import com.example.in2000_papirfly.data.repositories.FlightPathRepository
import com.example.in2000_papirfly.data.repositories.LoadOutRepository
import com.example.in2000_papirfly.data.repositories.PlaneRepository
import com.example.in2000_papirfly.data.screenuistates.LogPoint
import com.example.in2000_papirfly.data.screenuistates.LogState
import com.example.in2000_papirfly.data.screenuistates.ThrowScreenState
import com.example.in2000_papirfly.data.screenuistates.ThrowScreenUIState
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.*
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.changeHighScoreMarkerToNormal
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawGoalMarker
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawPlanePath
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawStartMarker
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.emptyHighScoreMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
    val changeLocation: (locationPoint: GeoPoint, locationName: String) -> Unit,
    val markerFactory: (type: String, throwLocation: String, temporary: Boolean) -> Marker,
    val mapOverlay: MutableList<Overlay>,
    val mapController: IMapController,
    updateOnMoveMap: (() -> Unit) -> Unit,
    val setInteraction: (Boolean) -> Unit,
    val weatherRepository: DataBaseContentNegotiator,
    val planeRepository: PlaneRepository,
    val loadOutRepository: LoadOutRepository,
): ViewModel() {

    /**
     * The mutable state of the screen
     */
    private val _uiState = MutableStateFlow(ThrowScreenUIState())
    val uiState = _uiState.asStateFlow()

    /**
     * The mutable state of the plane
     */
    private val planeLogic = PlaneLogic(planeRepository)
    val planeState = planeLogic.planeState

    // Necessary to prevent a crash
    var planeFlying: Job = Job()

    // Weather at the current plane position
    var weather: Weather = Weather()

    init {
        updateOnMoveMap {
            if (uiState.value.uiState !is ThrowScreenState.MovingMap &&
                uiState.value.uiState !is ThrowScreenState.ViewingLog
            ) setThrowScreenState(
                ThrowScreenState.MovingMap
            )
        }
        mapController.setCenter(selectedLocation)
        mapController.setZoom(12.0)

        // Fetches updated weather data for all throw points
        fetchWeatherForAllThrowPoints()

        // Fetches weather for start location
        CoroutineScope(Dispatchers.IO).launch {
            weather = weatherRepository.getWeatherAtPoint(selectedLocation)
        }

        // Clears all overlays from the map, and then draws every cached flight path
        mapOverlay.clear()
        FlightPathRepository.flightPaths.forEach { path ->
            var previous = GeoPoint(0.0, 0.0)
            path.second.forEach { point ->
                if (path.second.indexOf(point) > 0) {
                    drawPlanePath(mapOverlay, previous, point)
                }
                previous = point
            }
        }

        FlightPathRepository.markers.forEach {
            if (it is GoalMarker) {
                drawGoalMarker(
                    markerFactory,
                    mapOverlay,
                    ThrowPointList.throwPoints.getOrDefault(
                        it.throwLocation,
                        GeoPoint(0.0, 0.0)
                    ),
                    it.throwLocation,
                    it.position,
                    it.highScore,
                    it.temporary
                )
            }
        }

        // Places every throw point
        ThrowPointList.throwPoints.forEach {
            val marker = drawStartMarker(
                markerFactory = markerFactory,
                getThrowScreenState = { getThrowScreenState() },
                setThrowScreenState = {
                    setThrowScreenState(ThrowScreenState.ChoosingPosition)
                },
                updateWeather = { fetchWeatherForAllThrowPoints() },
                moveLocation = {
                    moveLocation(it.value, it.key)
                },
                mapOverlay = mapOverlay,
                startPos = it.value,
                locationName = it.key,
            )
            FlightPathRepository.markers.add(marker)

            if (it.key == "Oslo") {
                marker.showInfoWindow()
            }
        }
        fetchUpdatedHighScores()
    }

    private fun redrawMapMarkers() {
        for (marker in FlightPathRepository.markers) {
            mapOverlay.remove(marker)
            mapOverlay.add(marker)
        }
    }

    fun moveLocation(newLocation: GeoPoint, newLocationName: String) {
        selectedLocation = newLocation
        locationName = newLocationName
        changeLocation(selectedLocation, locationName)
        CoroutineScope(Dispatchers.IO).launch {
            weather = weatherRepository.getWeatherAtPoint(selectedLocation)
        }
        fetchUpdatedHighScores()
        mapOverlay.forEach {
            if (it is ThrowPositionMarker && it.title == locationName) {
                it.showInfoWindow()
            }
        }
    }

    fun setThrowScreenState(state: ThrowScreenState) {
        Log.d("ThrowScreenState", "${state.javaClass}")
        _uiState.update {
            it.copy(
                uiState = state
            )
        }
    }

    private fun getThrowScreenState(): ThrowScreenState {
        return uiState.value.uiState
    }

    fun throwPlane() {
        //  TODO // Calculate or get the angle and speed the plane should be launched at
        val speed = 10.0
        val planeStartHeight = 100.0
        val flightPath = mutableListOf<GeoPoint>()
        flightPath.add(selectedLocation)

        var previousPlanePos = selectedLocation
        var nextPlanePos: GeoPoint

        setThrowScreenState(ThrowScreenState.Flying)
        mapController.setCenter(selectedLocation)

        // initialize starter plane
        planeRepository.update(
            planeState.value.copy(
                speed = speed,
                height = planeStartHeight,
                pos = listOf(selectedLocation.latitude, selectedLocation.longitude),
                flying = true
            )
        )
        // Make sure the selected attachments are applied
        addAttachments(planeRepository, loadOutRepository)

        // FLIGHT-LOG
        val logPoints = mutableListOf<LogPoint>()

        // Start the coroutine that updates the plane every second
        planeFlying = viewModelScope.launch {
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

                // FLIGHT-LOG
                val logPoint = LogPoint(
                    geoPoint = GeoPoint(planeState.value.pos[0], planeState.value.pos[1]),
                    weather = weather.copy(),
                    height = planeState.value.height,
                    speed = planeState.value.speed

                )

                logPoints.add(logPoint)

                // This fixes the map glitching
                previousPlanePos = GeoPoint(planeState.value.pos[0], planeState.value.pos[1])
            }

            planeLogic.update(weather)
            val distance = (selectedLocation.distanceToAsDouble(previousPlanePos) / 1000).toInt()
            val newHS = updateHighScore(distance, flightPath)

            // In case the previous high score is visible on the map
            if (newHS) changeHighScoreMarkerToNormal(
                markerFactory = markerFactory,
                mapOverlay = mapOverlay,
                startPos = selectedLocation,
                throwLocation = locationName,
            )

            // Draws goal flag
            val goalMarker = drawGoalMarker(
                markerFactory = markerFactory,
                mapOverlay = mapOverlay,
                startPos = selectedLocation,
                throwLocation = locationName,
                markerPos = previousPlanePos,
                newHS = newHS,
                temporary = false
            )

            FlightPathRepository.markers.add(goalMarker)

            FlightPathRepository.flightPaths.add(Pair(distance, flightPath))

            // Unlock map
            setInteraction(true)

            // Landing state?
            setThrowScreenState(ThrowScreenState.ViewingLog)

            // FLIGHT-LOG
            showLog(
                distance,
                newHS,
                logPoints
            )

            // Moves all markers in front of flight paths
            redrawMapMarkers()
        }
    }

    private fun showLog(
        distance: Int,
        newHS: Boolean,
        logPoints: MutableList<LogPoint>,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    logState = LogState(
                        isVisible = true,
                        distance = distance,
                        newHS = newHS,
                        logPoints = logPoints
                    )
                )
            }
        }
    }

    fun closeLog() {
        Log.d("Log", "Closing log")

        val newLogState = LogState(
            isVisible = false,
            distance = uiState.value.logState.distance,
            newHS = uiState.value.logState.newHS,
            logPoints = uiState.value.logState.logPoints
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    logState = newLogState
                )
            }
        }

        setThrowScreenState(ThrowScreenState.MovingMap)
    }

    private fun updateHighScore(
        distance: Int,
        flightPath: List<GeoPoint>
    ): Boolean {
        if (distance > uiState.value.throwPointHighScoreMap.getOrDefault(
                locationName,
                HighScore()
            ).distance
        ) {
            weatherRepository.updateHighScore(
                location = locationName,
                distance = distance,
                time = System.currentTimeMillis() / 1000L,
                path = flightPath
            ) { fetchUpdatedHighScores() }
            return true
        }
        return false
    }

    fun updateHighScoreShownState(location: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val highScoreMapCopy = mutableMapOf<String, Boolean>()
            uiState.value.highScoresShownOnMap.toMap(highScoreMapCopy)
            highScoreMapCopy[location] = !highScoreMapCopy.getOrDefault(location, true)
            Log.d("HighScores", "Updated shown value to ${highScoreMapCopy[location]}")

            _uiState.update {
                it.copy(
                    highScoresShownOnMap = highScoreMapCopy
                )
            }
        }
    }

    private fun fetchUpdatedHighScores() {
        CoroutineScope(Dispatchers.IO).launch {
            val updatedHighScores = emptyHighScoreMap()
            updatedHighScores.forEach {(k) ->
                updatedHighScores[k] = weatherRepository.getHighScore(k)
            }

            _uiState.update {
                it.copy(
                    throwPointHighScoreMap = updatedHighScores
                )
            }
        }
    }

    private fun fetchWeatherForAllThrowPoints() {
        CoroutineScope(Dispatchers.IO).launch {
            val newWeather = weatherRepository.getThrowPointWeatherList()
            _uiState.update {
                it.copy(
                    throwPointWeatherList = newWeather
                )
            }
        }
    }

    fun resetPlane() {
        planeRepository.update(Plane())
        addAttachments(planeRepository, loadOutRepository)
    }

    fun changeAngle(value: Float) {
        if (uiState.value.uiState !is ThrowScreenState.Throwing) {
            CoroutineScope(Dispatchers.IO).launch {
                weather = weatherRepository.getWeatherAtPoint(selectedLocation)
            }
            setThrowScreenState(ThrowScreenState.Throwing)
            mapController.setCenter(selectedLocation)
            mapController.setZoom(12.0)
        }
        planeRepository.update(planeState.value.copy(angle = value.toDouble()))
    }

    fun getPlaneScale(): Float {
        // This should not be done here like this I think.
        return planeLogic.getPlaneScale()
    }

    private fun planeIsFlying(): Boolean {
        return planeState.value.flying
    }
}

class ThrowViewModelFactory(
    val weatherRepository: DataBaseContentNegotiator,
    val planeRepository: PlaneRepository,
    val loadOutRepository: LoadOutRepository,
){
    fun newViewModel(
        locationName: String,
        selectedLocation: GeoPoint,
        mapViewState: DisableMapView,
        openBottomSheet: (Int) -> Unit,
        changeLocation: (locationPoint: GeoPoint, locationName: String) -> Unit,

    ): ThrowViewModel{
        return ThrowViewModel(
            locationName = locationName,
            selectedLocation = selectedLocation,
            changeLocation = changeLocation,
            markerFactory = { type, throwLocation, temporary ->
                when(type) {
                    "Start" -> return@ThrowViewModel ThrowPositionMarker(
                        mapViewState,
                        openBottomSheet,
                        throwLocation
                    )
                    "HighScore" -> return@ThrowViewModel GoalMarker(
                        mapViewState,
                        throwLocation,
                        true,
                        temporary
                    )
                    else -> return@ThrowViewModel GoalMarker(
                        mapViewState,
                        throwLocation,
                        false,
                        temporary
                    )
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
            weatherRepository = weatherRepository,
            loadOutRepository = loadOutRepository,
        )
    }
}