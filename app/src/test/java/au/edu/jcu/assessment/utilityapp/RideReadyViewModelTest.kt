package au.edu.jcu.assessment.utilityapp

import androidx.lifecycle.SavedStateHandle
import au.edu.jcu.assessment.utilityapp.data.EnvironmentData
import au.edu.jcu.assessment.utilityapp.data.EnvironmentRepository
import au.edu.jcu.assessment.utilityapp.ui.RideReadyViewModel
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RideReadyViewModelTest {

    private val testEnvironment = EnvironmentData(
        locationName = "Test location",
        temperatureCelsius = 20.0,
        usAqi = 25,
        windSpeedKmh = 8,
        rainProbabilityPercent = 10
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeEnvironmentRepository(
        var load: suspend () -> EnvironmentData
    ) : EnvironmentRepository {

        var requestCount = 0
            private set

        override suspend fun getEnvironment(): EnvironmentData {
            requestCount++
            return load()
        }
    }

    private fun createViewModel(
        repository: EnvironmentRepository = FakeEnvironmentRepository {
            testEnvironment
        },
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        initialDarkTheme: Boolean = false
    ) = RideReadyViewModel(
        repository = repository,
        savedStateHandle = savedStateHandle,
        initialDarkTheme = initialDarkTheme
    )

    @Test
    fun initialLoad_showsLoadingThenRepositoryData() = runTest {
        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.environment)
        assertNull(viewModel.uiState.value.temperature)

        advanceUntilIdle()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(testEnvironment, state.environment)
        assertEquals(20.0, requireNotNull(state.temperature), 0.001)
        assertEquals("°C", state.temperatureUnit)
        assertFalse(state.useFahrenheit)
        assertFalse(state.useDarkTheme)
        assertFalse(state.isDemoRideActive)
    }

    @Test
    fun toggleDemoRide_preservesPreferences() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
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
    fun changeUnit_convertsBothWaysAndPreservesRide() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.toggleDemoRide()

        viewModel.setUseFahrenheit(true)

        assertEquals(
            68.0,
            requireNotNull(viewModel.uiState.value.temperature),
            0.001
        )
        assertEquals("°F", viewModel.uiState.value.temperatureUnit)
        assertTrue(viewModel.uiState.value.isDemoRideActive)

        viewModel.setUseFahrenheit(false)

        assertEquals(
            20.0,
            requireNotNull(viewModel.uiState.value.temperature),
            0.001
        )
        assertEquals("°C", viewModel.uiState.value.temperatureUnit)
        assertTrue(viewModel.uiState.value.isDemoRideActive)
        assertEquals(testEnvironment, viewModel.uiState.value.environment)
    }

    @Test
    fun changeTheme_preservesUnitAndActiveRide() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.toggleDemoRide()
        viewModel.setUseFahrenheit(true)

        viewModel.setUseDarkTheme(true)
        assertTrue(viewModel.uiState.value.useDarkTheme)

        viewModel.setUseDarkTheme(false)

        assertFalse(viewModel.uiState.value.useDarkTheme)
        assertTrue(viewModel.uiState.value.useFahrenheit)
        assertTrue(viewModel.uiState.value.isDemoRideActive)
    }

    @Test
    fun initialTheme_usesProvidedSystemPreference() = runTest {
        val viewModel = createViewModel(initialDarkTheme = true)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.useDarkTheme)
    }

    @Test
    fun savedValues_restoreWithFreshWeather() = runTest {
        val originalHandle = SavedStateHandle()
        val original = createViewModel(savedStateHandle = originalHandle)
        advanceUntilIdle()

        original.setUseFahrenheit(true)
        original.setUseDarkTheme(true)
        original.toggleDemoRide()

        // Recreate saved values; this is not a full Android process-death test.
        val savedValues = originalHandle.keys().associateWith { key ->
            originalHandle.get<Any?>(key)
        }

        val freshEnvironment = testEnvironment.copy(
            temperatureCelsius = 25.0
        )

        val restored = createViewModel(
            repository = FakeEnvironmentRepository { freshEnvironment },
            savedStateHandle = SavedStateHandle(savedValues)
        )
        advanceUntilIdle()

        val state = restored.uiState.value

        assertTrue(state.useFahrenheit)
        assertTrue(state.useDarkTheme)
        assertTrue(state.isDemoRideActive)
        assertEquals(freshEnvironment, state.environment)
        assertEquals(77.0, requireNotNull(state.temperature), 0.001)
    }

    @Test
    fun failedInitialLoad_canRetrySuccessfully() = runTest {
        val repository = FakeEnvironmentRepository {
            throw IOException("Simulated offline connection")
        }
        val viewModel = createViewModel(repository = repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.environment)
        assertNotNull(viewModel.uiState.value.errorMessage)

        repository.load = { testEnvironment }
        viewModel.refreshWeather()

        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)

        advanceUntilIdle()

        assertEquals(testEnvironment, viewModel.uiState.value.environment)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun failedRefresh_keepsPreviousWeatherAndUserState() = runTest {
        val repository = FakeEnvironmentRepository { testEnvironment }
        val viewModel = createViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.setUseFahrenheit(true)
        viewModel.setUseDarkTheme(true)
        viewModel.toggleDemoRide()

        repository.load = {
            throw IOException("Simulated refresh failure")
        }
        viewModel.refreshWeather()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(testEnvironment, state.environment)
        assertNotNull(state.errorMessage)
        assertFalse(state.isLoading)
        assertTrue(state.useFahrenheit)
        assertTrue(state.useDarkTheme)
        assertTrue(state.isDemoRideActive)
    }

    @Test
    fun pendingRequest_preservesNewUserActionsAndIgnoresDuplicateRefresh() =
        runTest {
            val response = CompletableDeferred<EnvironmentData>()
            val repository = FakeEnvironmentRepository { response.await() }
            val viewModel = createViewModel(repository = repository)
            runCurrent()

            assertTrue(viewModel.uiState.value.isLoading)

            viewModel.setUseFahrenheit(true)
            viewModel.setUseDarkTheme(true)
            viewModel.toggleDemoRide()
            viewModel.refreshWeather()
            runCurrent()

            assertEquals(1, repository.requestCount)

            val freshEnvironment = testEnvironment.copy(
                temperatureCelsius = 25.0
            )
            response.complete(freshEnvironment)
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(freshEnvironment, state.environment)
            assertTrue(state.useFahrenheit)
            assertTrue(state.useDarkTheme)
            assertTrue(state.isDemoRideActive)
            assertEquals(77.0, requireNotNull(state.temperature), 0.001)
            assertFalse(state.isLoading)
        }

    @Test
    fun cancellation_isNotShownAsNetworkFailure() = runTest {
        val repository = FakeEnvironmentRepository {
            throw CancellationException("Simulated cancellation")
        }
        val viewModel = createViewModel(repository = repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.environment)
    }
}