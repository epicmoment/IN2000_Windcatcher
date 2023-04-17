package com.example.in2000_papirfly.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.network.getLocationforecastData
import com.example.in2000_papirfly.network.getNowcastData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherState (
    val oslo : Weather = Weather(),
    val stavanger : Weather = Weather(),
    val galdhopiggen : Weather = Weather(),
    val lastUpdateUnix : Long = 0
)

// Halla!
// Å skrive weatherrepository som viewmodel funker til MVP-formål, men
// den skrives om totalt etter det, gjerne med Jetpack Room som database
//
// En instans av WeatherRepositoryMVP blir laget i NavScreen. Referanse til den eller
// en lambda-funksjon som kaller på getWeatherAt kan passes til skjermene og deres
// viewmodels
class WeatherRepositoryMVP : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState : StateFlow<WeatherState> = _weatherState.asStateFlow()

    var isUpdating = false

    // Bruker stedsnavn til MVPen i stedet for Location eller GeoPoint

    fun getWeatherAt(locationName : String) : Weather {

        // hvis 5 min siden forrige oppdatering
        if (System.currentTimeMillis() / 1000L - weatherState.value.lastUpdateUnix > 300 && !isUpdating) {
            isUpdating = true
            updateWeather()
        }

        return when (locationName) {
            "Oslo" -> weatherState.value.oslo
            "Stavanger" -> weatherState.value.stavanger
            "Galdhøpiggen" -> weatherState.value.galdhopiggen
            else -> Weather()
        }

    }

    // Added to prevent the APIs from being called too many times at once from PositionScreen
    init {
        updateWeather()
    }

    suspend fun getWeatherAtPoint(latitude : Double, longitude : Double) : Weather {

        val pointData = getNowcastData(
            lat = latitude,
            lon = longitude
        ).properties.timeseries[0].data.instant.details

        return Weather(
            windSpeed = pointData.wind_speed,
            windAngle = pointData.wind_from_direction,
            rain = pointData.precipitation_rate,
            temperature = pointData.air_temperature
        )

    }

    // dette er helt forferdelig, i know. Gjentakende kode, hardkoding, outofbounds, osv
    // Midlertidig for MVPen lol
    private fun updateWeather() {

        viewModelScope.launch {

            val osloData : LocationforecastData = getLocationforecastData(
                lat = 59.944030,
                lon = 10.719282
            )

            val stavData : LocationforecastData = getLocationforecastData(
                lat = 58.89729,
                lon = 5.71185
            )

            val galdData : LocationforecastData = getLocationforecastData(
                lat = 61.63630,
                lon = 8.31289
            )

            val osloWeather = Weather(
                windSpeed = osloData.properties.timeseries[0].data.instant.details.wind_speed,
                windAngle = osloData.properties.timeseries[0].data.instant.details.wind_from_direction,
                rain = osloData.properties.timeseries[0].data.next_1_hours.details.precipitation_amount,
                temperature = osloData.properties.timeseries[0].data.instant.details.air_temperature,
                icon = osloData.properties.timeseries[0].data.next_1_hours.summary.symbol_code
            )

            val stavangerWeather = Weather(
                windSpeed = stavData.properties.timeseries[0].data.instant.details.wind_speed,
                windAngle = stavData.properties.timeseries[0].data.instant.details.wind_from_direction,
                rain = stavData.properties.timeseries[0].data.next_1_hours.details.precipitation_amount,
                temperature = stavData.properties.timeseries[0].data.instant.details.air_temperature,
                icon = stavData.properties.timeseries[0].data.next_1_hours.summary.symbol_code
            )

            val galdhopiggenWeather = Weather(
                windSpeed = galdData.properties.timeseries[0].data.instant.details.wind_speed,
                windAngle = galdData.properties.timeseries[0].data.instant.details.wind_from_direction,
                rain = galdData.properties.timeseries[0].data.next_1_hours.details.precipitation_amount,
                temperature = galdData.properties.timeseries[0].data.instant.details.air_temperature,
                icon = galdData.properties.timeseries[0].data.next_1_hours.summary.symbol_code
            )

            val unixTimeStamp = System.currentTimeMillis() / 1000L

            _weatherState.update {
                it.copy(
                    oslo = osloWeather,
                    stavanger = stavangerWeather,
                    galdhopiggen = galdhopiggenWeather,
                    lastUpdateUnix = unixTimeStamp
                )
            }

            isUpdating = false

        }
    }
}