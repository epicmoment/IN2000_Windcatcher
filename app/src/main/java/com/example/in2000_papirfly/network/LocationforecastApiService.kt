package com.example.in2000_papirfly.network

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

suspend fun getLocationforecastData(lat: Double, lon: Double): LocationforecastData {

    return locationforecastClient.get(LocationforecastURL.urlBuilder(lat, lon)) {
        headers {
            append("X-Gravitee-Api-Key", "c473e19e-965e-4c53-8408-5c4cb9622403")
        }
    }.body()
}