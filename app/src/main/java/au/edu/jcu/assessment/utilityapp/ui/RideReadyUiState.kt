package au.edu.jcu.assessment.utilityapp.ui

import au.edu.jcu.assessment.utilityapp.data.EnvironmentData

data class RideReadyUiState(
    val environment: EnvironmentData,
    val useFahrenheit: Boolean = false,
    val useDarkTheme: Boolean = false,
    val isDemoRideActive: Boolean = false
) {
    val temperature: Double
        get() = if (useFahrenheit) {
            environment.temperatureCelsius * 9.0 / 5.0 + 32.0
        } else {
            environment.temperatureCelsius
        }

    val temperatureUnit: String
        get() = if (useFahrenheit) "°F" else "°C"
}