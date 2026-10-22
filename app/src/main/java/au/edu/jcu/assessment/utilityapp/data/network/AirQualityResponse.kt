package au.edu.jcu.assessment.utilityapp.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AirQualityResponse(
    val current: CurrentAirQualityDto? = null
)

@Serializable
data class CurrentAirQualityDto(
    val time: Long? = null,

    @SerialName("us_aqi")
    val usAqi: Double? = null
)