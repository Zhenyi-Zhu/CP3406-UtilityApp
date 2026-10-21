package au.edu.jcu.assessment.utilityapp.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val current: CurrentWeatherDto? = null,
    val hourly: HourlyWeatherDto? = null
)

@Serializable
data class CurrentWeatherDto(
    val time: Long? = null,

    @SerialName("temperature_2m")
    val temperatureCelsius: Double? = null,

    @SerialName("wind_speed_10m")
    val windSpeedKmh: Double? = null
)

@Serializable
data class HourlyWeatherDto(
    val time: List<Long>? = null,

    @SerialName("precipitation_probability")
    val rainProbabilityPercent: List<Int?>? = null
)