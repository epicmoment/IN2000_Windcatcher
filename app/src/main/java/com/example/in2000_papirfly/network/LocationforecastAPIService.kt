package com.example.in2000_papirfly.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

object URL {
    private const val BASE_URL =
        "https://api.met.no/weatherapi/nowcast/2.0/complete?"

    fun URLBuilder(lat: Int, long: Int): String {
        return "${BASE_URL}lat=${lat}&lon=${long}"
    }
}

val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json()
    }
    install(UserAgent) {
        agent = "IN2000 - sivertfm@uio.no"
    }
}