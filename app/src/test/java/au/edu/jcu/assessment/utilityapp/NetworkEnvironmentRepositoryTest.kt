package au.edu.jcu.assessment.utilityapp

import au.edu.jcu.assessment.utilityapp.data.NetworkEnvironmentRepository
import au.edu.jcu.assessment.utilityapp.data.network.AirQualityApi
import au.edu.jcu.assessment.utilityapp.data.network.AirQualityResponse
import au.edu.jcu.assessment.utilityapp.data.network.CurrentAirQualityDto
import au.edu.jcu.assessment.utilityapp.data.network.CurrentWeatherDto
import au.edu.jcu.assessment.utilityapp.data.network.HourlyWeatherDto
import au.edu.jcu.assessment.utilityapp.data.network.WeatherApi
import au.edu.jcu.assessment.utilityapp.data.network.WeatherResponse
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class NetworkEnvironmentRepositoryTest {

    private val weatherTime = 1_800_000_900L
    private val airQualityTime = weatherTime - 900L
    private val rainPeriodEnd = weatherTime + 2700L

    private val weatherResponse = WeatherResponse(
        current = CurrentWeatherDto(
            time = weatherTime,
            temperatureCelsius = 28.0,
            windSpeedKmh = 10.4
        ),
        hourly = HourlyWeatherDto(
            time = listOf(airQualityTime, rainPeriodEnd),
            rainProbabilityPercent = listOf(5, 70)
        )
    )

    private fun createRepository(
        loadAirQuality: suspend () -> AirQualityResponse
    ) = NetworkEnvironmentRepository(
        weatherApi = FakeWeatherApi(weatherResponse),
        airQualityApi = FakeAirQualityApi(loadAirQuality)
    )

    @Test
    fun successfulRequests_combineValuesAndKeepSeparateTimes() = runTest {
        val repository = createRepository {
            AirQualityResponse(
                current = CurrentAirQualityDto(
                    time = airQualityTime,
                    usAqi = 49.6
                )
            )
        }

        val result = repository.getEnvironment()

        assertEquals(28.0, result.temperatureCelsius, 0.001)
        assertEquals(10, requireNotNull(result.windSpeedKmh))
        assertEquals(70, requireNotNull(result.rainProbabilityPercent))
        assertEquals(
            rainPeriodEnd,
            requireNotNull(result.rainPeriodEndEpochSeconds)
        )
        assertEquals(50, requireNotNull(result.usAqi))
        assertEquals(
            weatherTime,
            requireNotNull(result.weatherTimeEpochSeconds)
        )
        assertEquals(
            airQualityTime,
            requireNotNull(result.airQualityTimeEpochSeconds)
        )
    }

    @Test
    fun airQualityFailure_keepsWeatherAvailable() = runTest {
        val repository = createRepository {
            throw IOException("Simulated air quality service failure")
        }

        val result = repository.getEnvironment()

        assertEquals(28.0, result.temperatureCelsius, 0.001)
        assertEquals(10, requireNotNull(result.windSpeedKmh))
        assertEquals(70, requireNotNull(result.rainProbabilityPercent))
        assertEquals(
            weatherTime,
            requireNotNull(result.weatherTimeEpochSeconds)
        )
        assertNull(result.usAqi)
        assertNull(result.airQualityTimeEpochSeconds)
    }

    @Test
    fun missingOrInvalidAirQuality_isUnavailableInsteadOfZero() = runTest {
        val invalidResponses = listOf(
            AirQualityResponse(),
            AirQualityResponse(
                CurrentAirQualityDto(airQualityTime, null)
            ),
            AirQualityResponse(
                CurrentAirQualityDto(airQualityTime, -1.0)
            ),
            AirQualityResponse(
                CurrentAirQualityDto(airQualityTime, Double.NaN)
            ),
            AirQualityResponse(
                CurrentAirQualityDto(null, 42.0)
            ),
            AirQualityResponse(
                CurrentAirQualityDto(0L, 42.0)
            )
        )

        for (response in invalidResponses) {
            val repository = createRepository { response }
            val result = repository.getEnvironment()

            assertEquals(28.0, result.temperatureCelsius, 0.001)
            assertNull(result.usAqi)
            assertNull(result.airQualityTimeEpochSeconds)
        }
    }

    @Test
    fun airQualityCancellation_isPropagated() = runTest {
        val repository = createRepository {
            throw CancellationException("Simulated cancellation")
        }

        try {
            repository.getEnvironment()
            fail("Cancellation should propagate instead of returning partial data.")
        } catch (expected: CancellationException) {
            assertEquals("Simulated cancellation", expected.message)
        }
    }

    private class FakeWeatherApi(
        private val response: WeatherResponse
    ) : WeatherApi {

        override suspend fun getWeather(
            latitude: Double,
            longitude: Double,
            current: String,
            hourly: String,
            forecastHours: Int,
            temperatureUnit: String,
            windSpeedUnit: String,
            timezone: String,
            timeFormat: String
        ): WeatherResponse = response
    }

    private class FakeAirQualityApi(
        private val load: suspend () -> AirQualityResponse
    ) : AirQualityApi {

        override suspend fun getAirQuality(
            latitude: Double,
            longitude: Double,
            current: String,
            domains: String,
            timezone: String,
            timeFormat: String
        ): AirQualityResponse = load()
    }
}