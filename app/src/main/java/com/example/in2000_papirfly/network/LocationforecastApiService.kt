package com.example.in2000_papirfly.network

import android.util.Log
import com.example.in2000_papirfly.data.LocationforecastData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

object LocationforecastURL {
    private const val BASE_URL =
        "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/compact?"

    fun urlBuilder(lat: Double, lon: Double): String {
        return "${BASE_URL}lat=${lat}&lon=${lon}"
    }
}

val locationforecastClient = HttpClient(Android) {
    install(ContentNegotiation) {
        json()
    }
    install(UserAgent) {
        agent = "Team 03"
    }
}

/**
 * Fetches the weather at the given coordinates
 * asynchronously from the Locationforecast API.
 * All coordinates will be rounded to four decimals.
 *
 * @param lat: Double representing the latitude of the position
 * @param lon: Double representing the longitude of the position
 *
 * @return Weather data for the given coordinates as a
 * LocationforecastData-object
 */
suspend fun getLocationforecastData(lat: Double, lon: Double): LocationforecastData? {
    val roundedLat = kotlin.math.round(lat * 10000.0) / 10000.0
    val roundedLon = kotlin.math.round(lon * 10000.0) / 10000.0

    val response = locationforecastClient.get(LocationforecastURL.urlBuilder(roundedLat, roundedLon)) {
        headers {
            append("X-Gravitee-Api-Key", "c473e19e-965e-4c53-8408-5c4cb9622403")
        }
    }

    Log.i("HTTP response", "${response.status.value}")

    return if (response.status.value in 200..299) {
        response.body()
    } else {
        LocationforecastData()
    }
}