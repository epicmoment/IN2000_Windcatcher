package com.example.in2000_papirfly.data

import kotlinx.serialization.Serializable

@Serializable
data class LocationforecastData(
    val geometry: LFGeometry = LFGeometry(),
    val properties: LFProperties = LFProperties(),
    val type: String = ""
)

@Serializable
data class LFGeometry(
    val coordinates: List<String> = listOf(""),
    val type: String = ""
)

@Serializable
data class LFProperties(
    val meta: LFMeta = LFMeta(),
    val timeseries: List<LFWeatherTime> = listOf(LFWeatherTime())
)

@Serializable
data class LFMeta(
    val units: LFUnits = LFUnits(),
    val updated_at: String = ""
)

@Serializable
data class LFWeatherTime(
    val data: LFData = LFData(),
    val time: String = ""
)

@Serializable
data class LFUnits(
    val air_pressure_at_sea_level: String = "",
    val air_temperature: String = "",
    val cloud_area_fraction: String = "",
    val precipitation_amount: String = "",
    val relative_humidity: String = "",
    val wind_from_direction: String = "",
    val wind_speed: String = ""
)

@Serializable
data class LFData(
    val instant: LFInstant = LFInstant(),
    val next_12_hours: LFNext12Hours? = null,
    val next_1_hours: LFNext1Hours = LFNext1Hours(),
    val next_6_hours: LFNext6Hours? = null
)

@Serializable
data class LFInstant(
    val details: LFDetails = LFDetails()
)

@Serializable
data class LFNext12Hours(
    val summary: LFSummary = LFSummary()
)

@Serializable
data class LFNext1Hours(
    val details: LFPrecipitationNext1Hours = LFPrecipitationNext1Hours(),
    val summary: LFSummary = LFSummary()
)

@Serializable
data class LFNext6Hours(
    val details: LFPrecipitationNext6Hours = LFPrecipitationNext6Hours(),
    val summary: LFSummary = LFSummary()
)

@Serializable
data class LFDetails(
    val air_pressure_at_sea_level: Double = 0.0,
    val air_temperature: Double = 0.0,
    val cloud_area_fraction: Double = 0.0,
    val relative_humidity: Double = 0.0,
    val wind_from_direction: Double = 0.0,
    val wind_speed: Double = 0.0
)

@Serializable
data class LFSummary(
    val symbol_code: String = ""
)

@Serializable
data class LFPrecipitationNext1Hours(
    val precipitation_amount: Double = 0.0
)

@Serializable
data class LFPrecipitationNext6Hours(
    val precipitation_amount: Double = 0.0
)