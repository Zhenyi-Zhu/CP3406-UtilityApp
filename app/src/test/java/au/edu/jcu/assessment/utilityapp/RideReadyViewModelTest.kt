package au.edu.jcu.assessment.utilityapp

import androidx.lifecycle.SavedStateHandle
import au.edu.jcu.assessment.utilityapp.data.EnvironmentData
import au.edu.jcu.assessment.utilityapp.data.EnvironmentRepository
import au.edu.jcu.assessment.utilityapp.ui.RideReadyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RideReadyViewModelTest {

    private val testEnvironment = EnvironmentData(
        locationName = "Test location",
        temperatureCelsius = 20.0,
        usAqi = 25,
        windSpeedKmh = 8,
        rainProbabilityPercent = 10
    )

    private class FakeEnvironmentRepository(
        private val environment: EnvironmentData
    ) : EnvironmentRepository {
        override fun getEnvironment(): EnvironmentData = environment
    }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        initialDarkTheme: Boolean = false,
        environment: EnvironmentData = testEnvironment
    ): RideReadyViewModel = RideReadyViewModel(
        repository = FakeEnvironmentRepository(environment),
        savedStateHandle = savedStateHandle,
        initialDarkTheme = initialDarkTheme
    )

    @Test
    fun initialState_usesRepositoryDataAndDefaults() {
        val state = createViewModel().uiState.value

        assertEquals(testEnvironment, state.environment)
        assertEquals(20.0, state.temperature, 0.001)
        assertEquals("°C", state.temperatureUnit)
        assertFalse(state.useFahrenheit)
        assertFalse(state.useDarkTheme)
        assertFalse(state.isDemoRideActive)
    }

    @Test
    fun toggleDemoRide_startsAndEndsRideWithoutChangingPreferences() {
        val viewModel = createViewModel()
        viewModel.setUseFahrenheit(true)
        viewModel.setUseDarkTheme(true)

        viewModel.toggleDemoRide()
        assertTrue(viewModel.uiState.value.isDemoRideActive)

        viewModel.toggleDemoRide()
        val state = viewModel.uiState.value
        assertFalse(state.isDemoRideActive)
        assertTrue(state.useFahrenheit)
        assertTrue(state.useDarkTheme)
    }

    @Test
    fun changeUnit_convertsBothWaysAndPreservesRide() {
        val viewModel = createViewModel()
        viewModel.toggleDemoRide()

        viewModel.setUseFahrenheit(true)
        assertEquals(68.0, viewModel.uiState.value.temperature, 0.001)
        assertEquals("°F", viewModel.uiState.value.temperatureUnit)
        assertTrue(viewModel.uiState.value.isDemoRideActive)

        viewModel.setUseFahrenheit(false)
        assertEquals(20.0, viewModel.uiState.value.temperature, 0.001)
        assertEquals("°C", viewModel.uiState.value.temperatureUnit)
        assertTrue(viewModel.uiState.value.isDemoRideActive)
        assertEquals(testEnvironment, viewModel.uiState.value.environment)
    }

    @Test
    fun changeTheme_preservesUnitAndActiveRide() {
        val viewModel = createViewModel()
        viewModel.toggleDemoRide()
        viewModel.setUseFahrenheit(true)

        viewModel.setUseDarkTheme(true)
        assertTrue(viewModel.uiState.value.useDarkTheme)
        assertTrue(viewModel.uiState.value.useFahrenheit)
        assertTrue(viewModel.uiState.value.isDemoRideActive)

        viewModel.setUseDarkTheme(false)
        assertFalse(viewModel.uiState.value.useDarkTheme)
        assertTrue(viewModel.uiState.value.useFahrenheit)
        assertTrue(viewModel.uiState.value.isDemoRideActive)
    }

    @Test
    fun initialTheme_usesProvidedSystemPreference() {
        val viewModel = createViewModel(initialDarkTheme = true)

        assertTrue(viewModel.uiState.value.useDarkTheme)
    }

    @Test
    fun savedValues_restoreIntoNewViewModelWithFreshEnvironment() {
        val originalHandle = SavedStateHandle()
        val original = createViewModel(savedStateHandle = originalHandle)
        original.setUseFahrenheit(true)
        original.setUseDarkTheme(true)
        original.toggleDemoRide()

        // Recreate from saved values; this does not simulate Android process death.
        val savedValues = originalHandle.keys().associateWith { key ->
            originalHandle.get<Any?>(key)
        }
        val freshEnvironment = testEnvironment.copy(temperatureCelsius = 25.0)
        val restored = createViewModel(
            savedStateHandle = SavedStateHandle(savedValues),
            initialDarkTheme = false,
            environment = freshEnvironment
        )
        val state = restored.uiState.value

        assertTrue(state.useFahrenheit)
        assertTrue(state.useDarkTheme)
        assertTrue(state.isDemoRideActive)
        assertEquals(freshEnvironment, state.environment)
        assertEquals(77.0, state.temperature, 0.001)
        assertEquals("°F", state.temperatureUnit)
    }
}