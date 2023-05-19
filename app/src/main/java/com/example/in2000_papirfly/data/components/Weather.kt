package com.example.in2000_papirfly.data.components

data class Weather(
    val windSpeed: Double = 0.0,
    val windAngle: Double = 0.0,
    val airPressure: Double = 0.0,
    val rain: Double = 0.0,
    val temperature : Double = 0.0,
    val icon : String = "cloudy",
    var namePos: String = "N/A"
)
