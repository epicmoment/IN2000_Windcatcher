package com.example.in2000_papirfly.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Bruker stedsnavn til MVPen i stedet for Location eller GeoPoint

    fun getWeatherAt(locationName : String) : Weather {

        // hvis 5 min siden forrige oppdatering
        if (System.currentTimeMillis() / 1000L - weatherState.value.lastUpdateUnix > 300) {
            updateWeather()
        }

        return when (locationName) {
            "Oslo" -> weatherState.value.oslo
            "Stavanger" -> weatherState.value.stavanger
            "Galdhøpiggen" -> weatherState.value.galdhopiggen
            else -> Weather()
        }

    }

    // dette er helt forferdelig, i know. Gjentakende kode, hardkoding, outofbounds, osv
    // Midlertidig for MVPen lol
    private fun updateWeather() {

        viewModelScope.launch {

            val osloData : NCDetails = getNowcastData(
                lat = 59.944030,
                lon = 10.719282
            ).properties.timeseries[0].data.instant.details

            val stavData : NCDetails = getNowcastData(
                lat = 58.89729,
                lon = 5.71185
            ).properties.timeseries[0].data.instant.details

            val galdData : NCDetails = getNowcastData(
                    lat = 61.63681,
                    lon = 8.31250
            ).properties.timeseries[0].data.instant.details

            val osloWeather = Weather(
                windSpeed = osloData.wind_speed,
                windAngle = osloData.wind_from_direction,
                rain = osloData.precipitation_rate,
                temperature = osloData.precipitation_rate
            )

            val stavangerWeather = Weather(
                windSpeed = stavData.wind_speed,
                windAngle = stavData.wind_from_direction,
                rain = stavData.precipitation_rate,
                temperature = stavData.precipitation_rate
            )

            val galdhopiggenWeather = Weather(
                windSpeed = galdData.wind_speed,
                windAngle = galdData.wind_from_direction,
                rain = galdData.precipitation_rate,
                temperature = galdData.precipitation_rate
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

        }

    }


}