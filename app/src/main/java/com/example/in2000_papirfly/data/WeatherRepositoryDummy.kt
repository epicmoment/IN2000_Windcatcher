package com.example.in2000_papirfly.data

import org.osmdroid.util.GeoPoint
import kotlin.random.Random

/**
 * A dummy object with fairly random weather data.
 */
data class WeatherRepositoryDummy(
    val temperature: Double = Random.nextDouble(-30.0, 30.0),
    val windSpeed: Double = Random.nextDouble(0.0, 55.0),
    val windFromDirection: Double = Random.nextDouble(0.0, 359.9),
    val precipitationAmount: Double = Random.nextDouble(0.0, 50.0),
    val namePos: String,
    val geoPoint: GeoPoint,
)