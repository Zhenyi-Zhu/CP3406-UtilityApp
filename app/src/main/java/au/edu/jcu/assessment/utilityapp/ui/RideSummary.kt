package au.edu.jcu.assessment.utilityapp.ui

import java.util.Locale

data class RideSummary(
    val notice: String,
    val items: List<String>
)

fun buildRideSummary(state: RideReadyUiState): RideSummary {
    val environment = state.environment ?: return RideSummary(
        notice = if (state.isLoading) {
            "Loading environment data…"
        } else {
            "Environment data is unavailable."
        },
        items = listOf(
            "Use the refresh or retry button below to load conditions."
        )
    )

    val temperature = state.temperature?.let {
        String.format(
            Locale.ENGLISH,
            "%.1f %s",
            it,
            state.temperatureUnit
        )
    } ?: "Unavailable"

    val temperatureText = if (environment.temperatureCelsius >= 30.0) {
        "Temperature: $temperature — bring water and plan breaks."
    } else {
        "Temperature: $temperature."
    }

    val rain = environment.rainProbabilityPercent
    val rainText = when {
        rain == null ->
            "Rain: unavailable — check a local forecast."
        rain >= 50 ->
            "Rain: $rain% — plan for wet conditions."
        else ->
            "Rain: $rain% — rain is still possible."
    }

    val wind = environment.windSpeedKmh
    val windText = when {
        wind == null ->
            "Wind: unavailable."
        wind >= 20 ->
            "Wind: $wind km/h — consider exposed parts of your route."
        else ->
            "Wind: $wind km/h."
    }

    val aqi = environment.usAqi
    val airText = if (aqi == null || aqi < 0) {
        "Air quality: unavailable — check local air quality information."
    } else {
        "US AQI $aqi: ${usAqiCategory(aqi)}."
    }

    val notice = when {
        environment.isSample ->
            "Sample data for preview."
        state.isLoading ->
            "Updating — this summary uses previously loaded values."
        state.errorMessage != null ->
            "Refresh failed — this summary uses previously loaded values."
        else ->
            "Based on model data. Check the separate timestamps below."
    }

    return RideSummary(
        notice = notice,
        items = listOf(
            temperatureText,
            rainText,
            windText,
            airText
        )
    )
}

fun usAqiCategory(aqi: Int): String = when {
    aqi < 0 -> "Unavailable"
    aqi <= 50 -> "Good"
    aqi <= 100 -> "Moderate"
    aqi <= 150 -> "Unhealthy for sensitive groups"
    aqi <= 200 -> "Unhealthy"
    aqi <= 300 -> "Very unhealthy"
    else -> "Hazardous"
}