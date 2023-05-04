package com.example.in2000_papirfly.data

import android.util.Log
import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.data.database.entities.FlightPathPoint
import com.example.in2000_papirfly.data.database.entities.ThrowPoint
import com.example.in2000_papirfly.data.database.entities.WeatherTile
import com.example.in2000_papirfly.network.getLocationforecastData
import com.example.in2000_papirfly.network.getNowcastData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import kotlin.math.roundToInt

class DataRepository(database: PapirflyDatabase) {

    private val roundingFactor = 10.0 // Rounds to one decimal point
    private val throwDao = database.throwPointDao()
    private val tileDao = database.weatherTileDao()
    private val flightDao = database.flightPathDao()
    private val throwPoints = ThrowPointList.throwPoints

    init {
        CoroutineScope(Dispatchers.IO).launch {
            asyncPopulate()
        }
    }

    /**
     * This function returns a Weather-object with live weather data for the given location
     */
    fun getWeatherAt(locationName: String): Weather {

        Log.d("Repo", "Fetching info for throw point at \"$locationName\"")
        val point = throwDao.getThrowPointInfo(locationName)!!
        val tile = tileDao.getTileAt(point.tileX, point.tileY)!!
        return Weather(
            windSpeed = tile.windSpeed,
            windAngle = tile.windDirection,
            airPressure = tile.airPressure,
            rain = tile.precipitation,
            temperature = tile.temperature,
            icon = tile.icon,
            namePos = locationName
        )
    }

     fun getThrowPointWeatherList(): List<Weather> {
         val list = mutableListOf<Weather>()
         CoroutineScope(Dispatchers.IO).launch {
             throwPoints.forEach {
                 list += getWeatherAt(it.key)
             }
         }
         return list
     }

    fun getThrowGeoPoint(locationName: String): GeoPoint {
        return throwPoints[locationName]!!
    }

    // TODO: Show a loading screen during population of database to prevent potential crash
    private suspend fun asyncPopulate() = coroutineScope {
        if (throwDao.getSize() < throwPoints.size) {
            throwPoints.forEach {
                launch {
                    Log.d("Repo","Populating throw point \"${it.key}\"")
                    getWeatherAtPoint(it.value)

                    val roundedLat = roundCoordinate(it.value.latitude)
                    val roundedLon = roundCoordinate(it.value.longitude)

                    if (tileDao.getTileAt(roundedLat, roundedLon) == null) {
                        Log.d("Database", "Unexpected null value")
                    }

                    throwDao.insert(
                        ThrowPoint(
                            it.key,
                            roundedLat,
                            roundedLon,
                        )
                    )
                }
            }
        }
    }

    fun getHighScore(locationName: String): HighScore {

        val throwPoint = throwDao.getThrowPointInfo(locationName)!!
        val flightPathInfo = flightDao.getFlightPath(locationName)
        val flightPathPoints: MutableList<GeoPoint> = mutableListOf()

        if (flightPathInfo.isNotEmpty()) {
            flightPathInfo.sortBy { it.number }
            flightPathInfo.forEach {
                flightPathPoints += GeoPoint(it.locX, it.locY)
            }
        }

        return HighScore(
            locationName,
            throwPoint.hSDate,
            throwPoint.hSDistance ?: 0,
            flightPathPoints
        )
    }

    fun updateHighScore(location: String, distance: Int, time: Long, path: List<GeoPoint>, updateHighscore: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val throwPoint = throwDao.getThrowPointInfo(location)!!
            throwDao.insert(
                ThrowPoint(
                    name = throwPoint.name,
                    tileX = throwPoint.tileX,
                    tileY = throwPoint.tileY,
                    hSDate = time,
                    hSDistance = distance
                )
            )
            flightDao.deleteFLightPath(location)
            path.forEach {
                flightDao.insert(
                    FlightPathPoint(
                        location = location,
                        number = path.indexOf(it),
                        it.latitude,
                        it.longitude,
                        null
                    )
                )
            }
            updateHighscore()
        }
    }

    private fun roundCoordinate(coordinate: Double): Double {
        return (coordinate * roundingFactor).roundToInt() / roundingFactor
    }

    /**
     * This function returns a Weather-object with live weather data for the given location
     *
     * **WARNING**: This function cannot be called from the main thread! Please notice that a
     * viewModelScope-Coroutine always runs on the main thread, and is thus not able to call
     * this function by itself. Runblocking will also not work from the main thread.
     * For best results, use **CoroutineScope(Dispatchers.IO).launch { ... }**
     *
     * @param point The GeoPoint for where you want to get weather data
     *
     * @return A Weather object with the latest weather data for the given point
     */
    suspend fun getWeatherAtPoint(point: GeoPoint): Weather {

        // TODO: show some kind of message to user if no connection

        val lat = roundCoordinate(point.latitude)
        val lon = roundCoordinate(point.longitude)

        var weatherTile = tileDao.getTileAt(lat, lon)

        Log.d("Repo", "Weather tile found: ${weatherTile.toString()}")

        // If no weather is saved for the tile, we get it from LocationForecast
        if (weatherTile == null) {
            val weather = getLocationforecastData(lat, lon)
            Log.d(
                "Repo",
                "No data for tile at $lat,$lon found in database, fetching from API"
            )

            tileDao.insert(
                WeatherTile(
                    lat,
                    lon,
                    System.currentTimeMillis() / 1000L,
                    System.currentTimeMillis() / 1000L,
                    weather.properties.timeseries[0].data.next_1_hours.summary.symbol_code,
                    weather.properties.timeseries[0].data.instant.details.air_pressure_at_sea_level,
                    weather.properties.timeseries[0].data.instant.details.air_temperature,
                    weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount,
                    weather.properties.timeseries[0].data.instant.details.wind_speed,
                    weather.properties.timeseries[0].data.instant.details.wind_from_direction,
                )
            )
            weatherTile = tileDao.getTileAt(lat, lon)!!
            Log.d("Repo", "Tile at ${weatherTile.locX},${weatherTile.locY} successfully added.")

        // If LocationForecast hasn't been called in the past hour, the tile is updated
        } else if (weatherTile.lastUpdatedLF + 3600 < ((System.currentTimeMillis() / 1000L))) {
            val weather = getLocationforecastData(lat, lon)
            Log.d(
                "Repo",
                "Tile at $lat,$lon timed out, fetching from Locationforecast"
            )

            if (weather.type != "") {
                tileDao.insert(
                    WeatherTile(
                        lat,
                        lon,
                        System.currentTimeMillis() / 1000L,
                        System.currentTimeMillis() / 1000L,
                        weather.properties.timeseries[0].data.next_1_hours.summary.symbol_code,
                        weather.properties.timeseries[0].data.instant.details.air_pressure_at_sea_level,
                        weather.properties.timeseries[0].data.instant.details.air_temperature,
                        weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount,
                        weather.properties.timeseries[0].data.instant.details.wind_speed,
                        weather.properties.timeseries[0].data.instant.details.wind_from_direction,
                    )
                )
                weatherTile = tileDao.getTileAt(lat, lon)!!

                Log.d("Repo", "Tile at ${weatherTile.locX},${weatherTile.locY} successfully updated.")
            }

        // If NowCast hasn't been called in the past five minutes, the tile is updated
        } else if (weatherTile.lastUpdatedNC + 300 < ((System.currentTimeMillis() / 1000L))) {
            val weather = getNowcastData(lat, lon)
            Log.d(
                "Repo",
                "Tile at $lat,$lon timed out, fetching from Nowcast"
            )

            if (weather.type != "") {
                tileDao.insert(
                    WeatherTile(
                        lat,
                        lon,
                        System.currentTimeMillis() / 1000L,
                        weatherTile.lastUpdatedLF,
                        weatherTile.icon,
                        weatherTile.airPressure,
                        weather.properties.timeseries[0].data.instant.details.air_temperature,
                        weather.properties.timeseries[0].data.instant.details.precipitation_rate,
                        weather.properties.timeseries[0].data.instant.details.wind_speed,
                        weather.properties.timeseries[0].data.instant.details.wind_from_direction,
                    )
                )
                weatherTile = tileDao.getTileAt(lat, lon)!!
                Log.d("Repo", "Tile at ${weatherTile.locX},${weatherTile.locY} successfully updated.")
            }
        }

        return Weather(
            windSpeed = weatherTile.windSpeed,
            windAngle = weatherTile.windDirection,
            airPressure = weatherTile.airPressure,
            rain = weatherTile.precipitation,
            temperature = weatherTile.temperature,
            icon = weatherTile.icon
        )
    }
}