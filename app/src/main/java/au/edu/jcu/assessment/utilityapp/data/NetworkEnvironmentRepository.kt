package au.edu.jcu.assessment.utilityapp.data

import au.edu.jcu.assessment.utilityapp.data.network.AirQualityApi
import au.edu.jcu.assessment.utilityapp.data.network.CurrentAirQualityDto
import au.edu.jcu.assessment.utilityapp.data.network.WeatherApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

class NetworkEnvironmentRepository(
    private val weatherApi: WeatherApi,
    private val airQualityApi: AirQualityApi
) : EnvironmentRepository {

    override suspend fun getEnvironment(): EnvironmentData = coroutineScope {
        // Fetch both services concurrently; an AQI failure can return partial data.
        val airQualityRequest = async { loadAirQualityOrNull() }

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

        // Each probability describes the hour ending at its timestamp.
        val rainIndex = hourlyTimes.indexOfFirst { endTime ->
            endTime > weatherTime &&
                    endTime - SECONDS_PER_HOUR <= weatherTime
        }

        val rainProbability = hourly?.rainProbabilityPercent
            ?.getOrNull(rainIndex)
            ?.takeIf { it in 0..100 }

        val airQuality = airQualityRequest.await()

        EnvironmentData(
            locationName = "Singapore",
            temperatureCelsius = temperature,
            usAqi = airQuality?.usAqi?.roundToInt(),
            windSpeedKmh = current.windSpeedKmh
                ?.takeIf { it.isFinite() && it >= 0 }
                ?.roundToInt(),
            rainProbabilityPercent = rainProbability,
            weatherTimeEpochSeconds = weatherTime,
            rainPeriodEndEpochSeconds = if (rainProbability != null) {
                hourlyTimes.getOrNull(rainIndex)
            } else {
                null
            },
            airQualityTimeEpochSeconds = airQuality?.time
        )
    }

    private suspend fun loadAirQualityOrNull(): CurrentAirQualityDto? {
        return try {
            val response = airQualityApi.getAirQuality(
                latitude = SINGAPORE_LATITUDE,
                longitude = SINGAPORE_LONGITUDE
            )

            val current = requireNotNull(response.current)
            val aqi = requireNotNull(current.usAqi)
            val time = requireNotNull(current.time)

            require(
                aqi.isFinite() &&
                        aqi >= 0 &&
                        aqi <= Int.MAX_VALUE.toDouble()
            )
            require(time > 0)

            current
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // Missing AQI stays unavailable; never substitute a reassuring zero.
            null
        }
    }

    companion object {
        private const val SINGAPORE_LATITUDE = 1.3521
        private const val SINGAPORE_LONGITUDE = 103.8198
        private const val SECONDS_PER_HOUR = 3600L
    }
}