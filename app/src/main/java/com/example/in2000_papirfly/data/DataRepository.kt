package com.example.in2000_papirfly.data

import android.util.Log
import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.data.database.entities.ThrowPoint
import com.example.in2000_papirfly.data.database.entities.WeatherTile
import com.example.in2000_papirfly.network.getLocationforecastData
import com.example.in2000_papirfly.network.getNowcastData
import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import kotlin.math.roundToInt

class DataRepository(database: PapirflyDatabase) {

    private val throwDao = database.throwPointDao()
    private val tileDao = database.weatherTileDao()
    private val flightDao = database.flightPathDao()
    private val throwPoints =
        mapOf(
            Pair("Oslo", GeoPoint(59.944030, 10.719282)),
            Pair("Stavanger", GeoPoint(58.89729, 5.71185)),
            Pair("Galdhøpiggen", GeoPoint(61.63630, 8.31289)),
            Pair("Nordkapp", GeoPoint(71.1705, 25.7842)),
            Pair("Lindesnes Fyr", GeoPoint(57.9828, 7.0466))
        )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            asyncPopulate()
        }
    }

    /**
     * This function returns a Weather-object with live weather data for the given location
     */
    fun getWeatherAt(locationName: String): Weather {
        var weather = Weather()

        Log.d("Repo", "Fetching info for throw point at \"$locationName\"")
        val point = throwDao.getThrowPointInfo(locationName)!!
        val tile = tileDao.getTileAt(point.tileX, point.tileY)!!
        weather = Weather(
            windSpeed = tile.windSpeed,
            windAngle = tile.windDirection,
            airPressure = tile.airPressure,
            rain = tile.precipitation,
            temperature = tile.temperature,
            icon = tile.icon,
            namePos = locationName
        )

        return weather
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

                    val roundedLat = (it.value.latitude * 100.0).roundToInt() / 100.0
                    val roundedLon = (it.value.longitude * 100.0).roundToInt() / 100.0

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

    /**
     * This function returns a Weather-object with live weather data for the given location
     *
     * **WARNING**: This function cannot be called from the main thread! Please notice that a
     * viewModelScope-Coroutine always runs on the main thread, and is thus not able to call
     * this function by itself. Runblocking will also not work from the main thread.
     * For best results, use CoroutineScope(Dispatchers.IO).launch { ... }
     */
    suspend fun getWeatherAtPoint(point: GeoPoint): Weather {

        // TODO: show some kind of message to user if no connection

        val lat = kotlin.math.round(point.latitude * 100.0).roundToInt() / 100.0
        val lon = kotlin.math.round(point.longitude * 100.0).roundToInt() / 100.0

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