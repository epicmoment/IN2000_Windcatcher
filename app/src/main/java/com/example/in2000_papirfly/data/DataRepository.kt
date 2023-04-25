package com.example.in2000_papirfly.data

import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.data.database.entities.WeatherTile
import com.example.in2000_papirfly.network.getLocationforecastData
import com.example.in2000_papirfly.network.getNowcastData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

//data class RepositoryState(
//    val lastUpdateUnix : Long = 0
//)

class DataRepository(database: PapirflyDatabase) {

//    private val _repositoryState = MutableStateFlow(RepositoryState())
//    val repositoryState: StateFlow<RepositoryState> = _repositoryState.asStateFlow()

    private val throwDao = database.throwPointDao()
    private val tileDao = database.weatherTileDao()
    private val flightDao = database.flightPathDao()

    fun getWeatherAt(locationName: String): Weather {
        return Weather()
    }

    suspend fun getWeatherAtPoint(point: GeoPoint): Weather {

        // TODO: show some kind of message to user if no connection

        val lat = kotlin.math.round(point.latitude * 100.0) / 100.0
        val lon = kotlin.math.round(point.longitude * 100.0) / 100.0

        var weatherTile = tileDao.getTileAt(lat, lon)

        if (weatherTile == null) {
            val weather = getLocationforecastData(lat, lon)

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
            weatherTile = tileDao.getTileAt(lat, lon)

        } else if (weatherTile.lastUpdatedLF > ((System.currentTimeMillis() / 1000L) + 3600)) {
            val weather = getLocationforecastData(lat, lon)

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
                weatherTile = tileDao.getTileAt(lat, lon)
            }

        } else if (weatherTile.lastUpdatedNC > ((System.currentTimeMillis() / 1000L) + 300)) {
            val weather = getNowcastData(lat, lon)

            if (weather.type != "") {
                tileDao.insert(
                    WeatherTile(
                        lat,
                        lon,
                        System.currentTimeMillis() / 1000L,
                        System.currentTimeMillis() / 1000L,
                        weatherTile.icon,
                        weatherTile.airPressure,
                        weather.properties.timeseries[0].data.instant.details.air_temperature,
                        weather.properties.timeseries[0].data.instant.details.precipitation_rate,
                        weather.properties.timeseries[0].data.instant.details.wind_speed,
                        weather.properties.timeseries[0].data.instant.details.wind_from_direction,
                    )
                )
                weatherTile = tileDao.getTileAt(lat, lon)
            }
        }

        return Weather(
            windSpeed = weatherTile!!.windSpeed,
            windAngle = weatherTile.windDirection,
            airPressure = weatherTile.airPressure,
            rain = weatherTile.precipitation,
            temperature = weatherTile.temperature,
            icon = weatherTile.icon
        )
    }
}