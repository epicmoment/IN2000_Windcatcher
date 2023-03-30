package com.example.in2000_papirfly.data

import kotlinx.serialization.Serializable

@Serializable
data class NowcastData(
    val geometry: NCGeometry,
    val properties: NCProperties,
    val type: String
)

@Serializable
data class NCGeometry(
    val coordinates: List<Double>,
    val type: String
)

@Serializable
data class NCProperties(
    val meta: NCMeta,
    val timeseries: List<NCWeatherTime>
)

@Serializable
data class NCMeta(
    val radar_coverage: String,
    val units: NCUnits,
    val updated_at: String
)

@Serializable
data class NCWeatherTime(
    val data: NCData,
    val time: String
)
@Serializable
data class NCUnits(
    val air_temperature: String,
    val precipitation_amount: String,
    val precipitation_rate: String,
    val relative_humidity: String,
    val wind_from_direction: String,
    val wind_speed: String,
    val wind_speed_of_gust: String
)

@Serializable
data class NCData(
    val instant: NCInstant,
    val next_1_hours: NCNext1Hours? = null
)

@Serializable
data class NCInstant(
    val details: NCDetails
)

@Serializable
data class NCNext1Hours(
    val details: NCPrecipitationNext1Hours,
    val summary: NCSummary
)

@Serializable
data class NCDetails(
    val air_temperature: Double = 0.0,
    val precipitation_rate: Double = 0.0,
    val relative_humidity: Double = 0.0,
    val wind_from_direction: Double = 0.0,
    val wind_speed: Double = 0.0,
    val wind_speed_of_gust: Double = 0.0
)

@Serializable
data class NCPrecipitationNext1Hours(
    val precipitation_amount: Double
)

@Serializable
data class NCSummary(
    val symbol_code: String
)