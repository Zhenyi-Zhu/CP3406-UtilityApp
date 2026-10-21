package au.edu.jcu.assessment.utilityapp.data

import au.edu.jcu.assessment.utilityapp.data.network.WeatherApi
import kotlin.math.roundToInt

class NetworkEnvironmentRepository(
    private val weatherApi: WeatherApi
) : EnvironmentRepository {

    override suspend fun getEnvironment(): EnvironmentData {
        val response = weatherApi.getWeather(
            latitude = SINGAPORE_LATITUDE,
            longitude = SINGAPORE_LONGITUDE
        )

        val current = requireNotNull(response.current) {
            "The response has no current weather."
        }

        val temperature = requireNotNull(current.temperatureCelsius) {
            "The response has no temperature."
        }

        val weatherTime = requireNotNull(current.time) {
            "The response has no weather time."
        }

        require(temperature.isFinite() && weatherTime > 0) {
            "The response contains invalid weather data."
        }

        val hourly = response.hourly
        val hourlyTimes = hourly?.time.orEmpty()

        // Each probability describes the hour ENDING at its timestamp.
        // Select the interval containing the current weather timestamp.
        val rainIndex = hourlyTimes.indexOfFirst { endTime ->
            endTime > weatherTime &&
                    endTime - SECONDS_PER_HOUR <= weatherTime
        }

        val rainProbability = hourly?.rainProbabilityPercent
            ?.getOrNull(rainIndex)
            ?.takeIf { it in 0..100 }

        return EnvironmentData(
            locationName = "Singapore",
            temperatureCelsius = temperature,
            usAqi = null,

            // Keep the existing display precision of whole km/h.
            windSpeedKmh = current.windSpeedKmh
                ?.takeIf { it.isFinite() && it >= 0 }
                ?.roundToInt(),

            rainProbabilityPercent = rainProbability,
            weatherTimeEpochSeconds = weatherTime,

            rainPeriodEndEpochSeconds = if (rainProbability != null) {
                hourlyTimes.getOrNull(rainIndex)
            } else {
                null
            }
        )
    }

    companion object {
        private const val SINGAPORE_LATITUDE = 1.3521
        private const val SINGAPORE_LONGITUDE = 103.8198
        private const val SECONDS_PER_HOUR = 3600L
    }
}