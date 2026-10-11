package au.edu.jcu.assessment.utilityapp.data

data class EnvironmentData(
    val locationName: String,
    val temperatureCelsius: Double,
    val usAqi: Int,
    val windSpeedKmh: Int,
    val rainProbabilityPercent: Int
)

