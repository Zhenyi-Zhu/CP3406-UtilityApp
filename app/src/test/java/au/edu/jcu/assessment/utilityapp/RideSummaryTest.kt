package au.edu.jcu.assessment.utilityapp

import au.edu.jcu.assessment.utilityapp.data.EnvironmentData
import au.edu.jcu.assessment.utilityapp.ui.RideReadyUiState
import au.edu.jcu.assessment.utilityapp.ui.buildRideSummary
import au.edu.jcu.assessment.utilityapp.ui.usAqiCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RideSummaryTest {

    private val environment = EnvironmentData(
        locationName = "Singapore",
        temperatureCelsius = 30.0,
        usAqi = 42,
        windSpeedKmh = 20,
        rainProbabilityPercent = 50
    )

    @Test
    fun aqiCategories_handleBothSidesOfEveryBoundary() {
        val cases = listOf(
            -1 to "Unavailable",
            0 to "Good",
            50 to "Good",
            51 to "Moderate",
            100 to "Moderate",
            101 to "Unhealthy for sensitive groups",
            150 to "Unhealthy for sensitive groups",
            151 to "Unhealthy",
            200 to "Unhealthy",
            201 to "Very unhealthy",
            300 to "Very unhealthy",
            301 to "Hazardous",
            501 to "Hazardous"
        )

        cases.forEach { (aqi, expected) ->
            assertEquals("AQI $aqi", expected, usAqiCategory(aqi))
        }
    }

    @Test
    fun reminders_includeThresholdsAndDoNotDependOnTemperatureUnit() {
        val atThreshold = RideReadyUiState(environment = environment)
        val belowThreshold = atThreshold.copy(
            environment = environment.copy(
                temperatureCelsius = 29.9,
                windSpeedKmh = 19,
                rainProbabilityPercent = 49
            )
        )

        listOf(false, true).forEach { fahrenheit ->
            val at = buildRideSummary(
                atThreshold.copy(useFahrenheit = fahrenheit)
            )
            val below = buildRideSummary(
                belowThreshold.copy(useFahrenheit = fahrenheit)
            )

            assertTrue(at.items[0].contains("bring water"))
            assertTrue(at.items[1].contains("plan for wet conditions"))
            assertTrue(at.items[2].contains("exposed parts"))

            assertFalse(below.items[0].contains("bring water"))
            assertFalse(below.items[1].contains("plan for wet conditions"))
            assertFalse(below.items[2].contains("exposed parts"))

            val expectedTemperature =
                if (fahrenheit) "86.0 °F" else "30.0 °C"
            assertTrue(at.items[0].contains(expectedTemperature))
        }
    }

    @Test
    fun missingOptionalValues_areUnavailableInsteadOfZeroOrGood() {
        val summary = buildRideSummary(
            RideReadyUiState(
                environment = environment.copy(
                    usAqi = null,
                    windSpeedKmh = null,
                    rainProbabilityPercent = null
                )
            )
        )

        assertTrue(summary.items[0].contains("30.0 °C"))
        assertTrue(summary.items[1].contains("unavailable"))
        assertTrue(summary.items[2].contains("unavailable"))
        assertTrue(summary.items[3].contains("unavailable"))
        assertFalse(summary.items.any { it.contains("US AQI 0") })
        assertFalse(summary.items.any { it.contains("Good") })
    }

    @Test
    fun noEnvironment_showsLoadingOrUnavailableWithoutInventingConditions() {
        val loading = buildRideSummary(
            RideReadyUiState(isLoading = true)
        )
        val failed = buildRideSummary(
            RideReadyUiState(errorMessage = "Connection failed")
        )

        assertEquals("Loading environment data…", loading.notice)
        assertEquals("Environment data is unavailable.", failed.notice)

        listOf(loading, failed).forEach { summary ->
            assertEquals(1, summary.items.size)
            assertTrue(summary.items.single().contains("refresh or retry"))
            assertFalse(summary.items.any { it.contains("Temperature:") })
            assertFalse(summary.items.any { it.contains("US AQI") })
        }
    }

    @Test
    fun refreshingAndFailedRefresh_labelPreviouslyLoadedConditions() {
        val state = RideReadyUiState(environment = environment)
        val original = buildRideSummary(state)
        val refreshing = buildRideSummary(
            state.copy(isLoading = true)
        )
        val failed = buildRideSummary(
            state.copy(errorMessage = "Connection failed")
        )

        assertEquals(original.items, refreshing.items)
        assertEquals(original.items, failed.items)

        assertTrue(refreshing.notice.startsWith("Updating"))
        assertTrue(refreshing.notice.contains("previously loaded"))
        assertTrue(failed.notice.startsWith("Refresh failed"))
        assertTrue(failed.notice.contains("previously loaded"))
        assertFalse(original.notice.contains("previously loaded"))
    }

    @Test
    fun sampleEnvironment_isExplicitlyLabelled() {
        val summary = buildRideSummary(
            RideReadyUiState(
                environment = environment.copy(isSample = true)
            )
        )

        assertEquals("Sample data for preview.", summary.notice)
        assertEquals(4, summary.items.size)
    }
}