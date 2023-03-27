package com.example.in2000_papirfly.data

import kotlinx.serialization.Serializable

@Serializable
data class LocationforecastData(
    val geometry: LFGeometry,
    val properties: LFProperties,
    val type: String
)

@Serializable
data class LFGeometry(
    val coordinates: List<String>,
    val type: String
)

@Serializable
data class LFProperties(
    val meta: LFMeta,
    val timeseries: List<LFWeatherTime>
)

@Serializable
data class LFMeta(
    val units: LFUnits,
    val updated_at: String
)

@Serializable
data class LFWeatherTime(
    val data: LFData,
    val time: String
)

@Serializable
data class LFUnits(
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val cloud_area_fraction: String,
    val precipitation_amount: String,
    val relative_humidity: String,
    val wind_from_direction: String,
    val wind_speed: String
)

@Serializable
data class LFData(
    val instant: LFInstant,
    val next_12_hours: LFNext12Hours? = null,
    val next_1_hours: LFNext1Hours? = null,
    val next_6_hours: LFNext6Hours? = null
)

@Serializable
data class LFInstant(
    val details: LFDetails
)

@Serializable
data class LFNext12Hours(
    val summary: LFSummary
)

@Serializable
data class LFNext1Hours(
    val details: LFPrecipitationNext1Hours,
    val summary: LFSummary
)

@Serializable
data class LFNext6Hours(
    val details: LFPrecipitationNext6Hours,
    val summary: LFSummary
)

@Serializable
data class LFDetails(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val cloud_area_fraction: Double,
    val relative_humidity: Double,
    val wind_from_direction: Double,
    val wind_speed: Double
)

@Serializable
data class LFSummary(
    val symbol_code: String
)

@Serializable
data class LFPrecipitationNext1Hours(
    val precipitation_amount: Double
)

@Serializable
data class LFPrecipitationNext6Hours(
    val precipitation_amount: Double
)