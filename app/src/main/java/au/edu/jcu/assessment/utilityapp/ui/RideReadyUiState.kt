package au.edu.jcu.assessment.utilityapp.ui

import au.edu.jcu.assessment.utilityapp.data.EnvironmentData

data class RideReadyUiState(
    val environment: EnvironmentData? = null,
    val useFahrenheit: Boolean = false,
    val useDarkTheme: Boolean = false,
    val isDemoRideActive: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val temperature: Double?
        get() {
            val celsius = environment?.temperatureCelsius ?: return null
            return if (useFahrenheit) {
                celsius * 9.0 / 5.0 + 32.0
            } else {
                celsius
            }
        }

    val temperatureUnit: String
        get() = if (useFahrenheit) "°F" else "°C"
}