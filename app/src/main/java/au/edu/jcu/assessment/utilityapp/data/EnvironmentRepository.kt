package au.edu.jcu.assessment.utilityapp.data

interface EnvironmentRepository {
    fun getEnvironment(): EnvironmentData
}

class SampleEnvironmentRepository : EnvironmentRepository {
    override fun getEnvironment(): EnvironmentData = EnvironmentData(
        locationName = "Singapore",
        temperatureCelsius = 30.0,
        usAqi = 42,
        windSpeedKmh = 12,
        rainProbabilityPercent = 30
    )
}