package au.edu.jcu.assessment.utilityapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import au.edu.jcu.assessment.utilityapp.data.EnvironmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RideReadyViewModel(
    repository: EnvironmentRepository,
    private val savedStateHandle: SavedStateHandle,
    initialDarkTheme: Boolean = false
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RideReadyUiState(
            environment = repository.getEnvironment(),
            useFahrenheit = savedStateHandle[KEY_FAHRENHEIT] ?: false,
            useDarkTheme = savedStateHandle[KEY_DARK_THEME] ?: initialDarkTheme,
            isDemoRideActive = savedStateHandle[KEY_ACTIVE_RIDE] ?: false
        )
    )

    val uiState: StateFlow<RideReadyUiState> = _uiState.asStateFlow()

    fun setUseFahrenheit(enabled: Boolean) {
        updateState(_uiState.value.copy(useFahrenheit = enabled))
    }

    fun setUseDarkTheme(enabled: Boolean) {
        updateState(_uiState.value.copy(useDarkTheme = enabled))
    }

    fun toggleDemoRide() {
        val currentState = _uiState.value
        updateState(
            currentState.copy(isDemoRideActive = !currentState.isDemoRideActive)
        )
    }

    private fun updateState(newState: RideReadyUiState) {
        // Save simple values so Android can restore them with the saved state.
        savedStateHandle[KEY_FAHRENHEIT] = newState.useFahrenheit
        savedStateHandle[KEY_DARK_THEME] = newState.useDarkTheme
        savedStateHandle[KEY_ACTIVE_RIDE] = newState.isDemoRideActive
        _uiState.value = newState
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