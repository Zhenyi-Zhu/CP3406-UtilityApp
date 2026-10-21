package au.edu.jcu.assessment.utilityapp.data

/** Environmental values, with null representing unavailable optional data. */
data class EnvironmentData(
    val locationName: String,
    val temperatureCelsius: Double,
    val usAqi: Int?,
    val windSpeedKmh: Int?,
    val rainProbabilityPercent: Int?,
    val weatherTimeEpochSeconds: Long? = null,
    val rainPeriodEndEpochSeconds: Long? = null,
    val isSample: Boolean = false
)