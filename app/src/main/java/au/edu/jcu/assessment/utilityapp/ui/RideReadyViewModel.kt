package au.edu.jcu.assessment.utilityapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import au.edu.jcu.assessment.utilityapp.data.EnvironmentRepository
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RideReadyViewModel(
    private val repository: EnvironmentRepository,
    private val savedStateHandle: SavedStateHandle,
    initialDarkTheme: Boolean = false
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RideReadyUiState(
            useFahrenheit = savedStateHandle[KEY_FAHRENHEIT] ?: false,
            useDarkTheme = savedStateHandle[KEY_DARK_THEME] ?: initialDarkTheme,
            isDemoRideActive = savedStateHandle[KEY_ACTIVE_RIDE] ?: false
        )
    )

    val uiState: StateFlow<RideReadyUiState> = _uiState.asStateFlow()

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            try {
                val environment = repository.getEnvironment()

                // Copy the latest state so a response cannot undo user actions.
                _uiState.update {
                    it.copy(environment = environment)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = weatherErrorMessage(error))
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun setUseFahrenheit(enabled: Boolean) {
        _uiState.update {
            it.copy(useFahrenheit = enabled)
        }
        savePreferences()
    }

    fun setUseDarkTheme(enabled: Boolean) {
        _uiState.update {
            it.copy(useDarkTheme = enabled)
        }
        savePreferences()
    }

    fun toggleDemoRide() {
        _uiState.update {
            it.copy(isDemoRideActive = !it.isDemoRideActive)
        }
        savePreferences()
    }

    private fun savePreferences() {
        val state = _uiState.value
        savedStateHandle[KEY_FAHRENHEIT] = state.useFahrenheit
        savedStateHandle[KEY_DARK_THEME] = state.useDarkTheme
        savedStateHandle[KEY_ACTIVE_RIDE] = state.isDemoRideActive
    }

    private fun weatherErrorMessage(error: Exception): String = when (error) {
        is IOException ->
            "Could not reach the weather service. Check your connection and retry."

        is HttpException ->
            if (error.code() == 429) {
                "Too many weather requests. Please wait a moment and retry."
            } else {
                "The weather service is unavailable. Please try again later."
            }

        is IllegalArgumentException ->
            "The weather response was incomplete or invalid. Please retry."

        else ->
            "Weather could not be loaded. Please retry."
    }

    companion object {
        private const val KEY_FAHRENHEIT = "use_fahrenheit"
        private const val KEY_DARK_THEME = "use_dark_theme"
        private const val KEY_ACTIVE_RIDE = "is_demo_ride_active"

        fun factory(
            repository: EnvironmentRepository,
            initialDarkTheme: Boolean
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RideReadyViewModel(
                    repository = repository,
                    savedStateHandle = createSavedStateHandle(),
                    initialDarkTheme = initialDarkTheme
                )
            }
        }
    }
}