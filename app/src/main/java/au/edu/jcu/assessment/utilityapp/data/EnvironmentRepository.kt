package au.edu.jcu.assessment.utilityapp.data

/** Implementations can obtain data asynchronously without changing the UI. */
interface EnvironmentRepository {
    suspend fun getEnvironment(): EnvironmentData
}

class SampleEnvironmentRepository : EnvironmentRepository {

    override suspend fun getEnvironment(): EnvironmentData = sampleData

    companion object {
        // Previews can read this directly without starting a coroutine.
        val sampleData = EnvironmentData(
            locationName = "Singapore",
            temperatureCelsius = 30.0,
            usAqi = 42,
            windSpeedKmh = 12,
            rainProbabilityPercent = 30,
            isSample = true
        )
    }
}