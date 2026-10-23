package au.edu.jcu.assessment.utilityapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.data.EnvironmentData
import au.edu.jcu.assessment.utilityapp.ui.RideReadyUiState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RideScreen(
    uiState: RideReadyUiState,
    onToggleDemoRide: () -> Unit,
    onRefreshWeather: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "RideReady",
            style = MaterialTheme.typography.headlineMedium
        )
        Text("Check your environment before a ride.")

        RideSummaryCard(uiState = uiState)

        EnvironmentCard(
            uiState = uiState,
            onRefreshWeather = onRefreshWeather
        )

        DemoRideCard(
            isDemoRideActive = uiState.isDemoRideActive,
            onToggleDemoRide = onToggleDemoRide
        )
    }
}

@Composable
private fun EnvironmentCard(
    uiState: RideReadyUiState,
    onRefreshWeather: () -> Unit
) {
    val environment = uiState.environment
    val airQualityMissing = environment != null && environment.usAqi == null

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Environment",
                style = MaterialTheme.typography.titleLarge
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    if (environment == null) {
                        "Loading Singapore environment data…"
                    } else {
                        "Updating environment data… Previous values are shown below."
                    }
                )
            }

            uiState.errorMessage?.let { message ->
                Text(
                    message,
                    color = MaterialTheme.colorScheme.error
                )

                if (environment != null) {
                    Text(
                        "Showing previously loaded data. Check the timestamps."
                    )
                }
            }

            if (
                airQualityMissing &&
                !uiState.isLoading &&
                uiState.errorMessage == null
            ) {
                Text(
                    "Air quality could not be loaded. Weather is available. Tap Retry to try again.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (environment != null) {
                WeatherDetails(
                    environment = environment,
                    temperature = uiState.temperature,
                    temperatureUnit = uiState.temperatureUnit
                )
            } else if (!uiState.isLoading) {
                Text("No weather data available.")
            }

            OutlinedButton(
                onClick = onRefreshWeather,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when {
                        uiState.isLoading -> "Updating…"
                        uiState.errorMessage != null || airQualityMissing -> "Retry"
                        else -> "Refresh environment"
                    }
                )
            }
        }
    }
}

@Composable
private fun WeatherDetails(
    environment: EnvironmentData,
    temperature: Double?,
    temperatureUnit: String
) {
    val uriHandler = LocalUriHandler.current

    Text(
        if (environment.isSample) {
            "${environment.locationName} · Sample data"
        } else {
            "${environment.locationName} · Fixed location"
        }
    )

    val temperatureText = temperature?.let {
        String.format(Locale.ENGLISH, "%.1f", it)
    } ?: "Unavailable"

    Text(
        "Temperature: $temperatureText $temperatureUnit",
        style = MaterialTheme.typography.titleLarge
    )

    Text(
        "Wind speed: " +
                (environment.windSpeedKmh?.let { "$it km/h" } ?: "Unavailable")
    )

    Text(
        "Rain probability: " +
                (environment.rainProbabilityPercent?.let { "$it%" } ?: "Unavailable")
    )

    environment.rainPeriodEndEpochSeconds?.let { end ->
        Text(
            "Rain forecast period: ${formatWeatherTime(end - 3600)} – " +
                    "${formatWeatherTime(end)} SGT",
            style = MaterialTheme.typography.bodySmall
        )
    }

    Text(
        environment.usAqi?.let { "Air quality: US AQI $it" }
            ?: "Air quality: unavailable"
    )

    environment.airQualityTimeEpochSeconds?.let { time ->
        Text(
            "Air quality valid at: ${formatWeatherTime(time)} SGT",
            style = MaterialTheme.typography.bodySmall
        )
    }

    Text(
        "US AQI scale; different from Singapore PSI.",
        style = MaterialTheme.typography.bodySmall
    )

    environment.weatherTimeEpochSeconds?.let { time ->
        Text(
            "Weather valid at: ${formatWeatherTime(time)} SGT",
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (environment.isSample) {
        Text("Sample values for preview only.")
    } else {
        Text(
            "Weather and air quality values are model estimates and forecasts.",
            style = MaterialTheme.typography.bodySmall
        )

        TextButton(
            onClick = {
                uriHandler.openUri("https://open-meteo.com/")
            }
        ) {
            Text("Weather data: Open-Meteo")
        }

        TextButton(
            onClick = {
                uriHandler.openUri(
                    "https://open-meteo.com/en/docs/air-quality-api"
                )
            }
        ) {
            Text("Air quality: CAMS via Open-Meteo")
        }
    }
}

private val weatherTimeFormatter = DateTimeFormatter
    .ofPattern("dd MMM, HH:mm", Locale.ENGLISH)
    .withZone(ZoneId.of("Asia/Singapore"))

private fun formatWeatherTime(epochSeconds: Long): String =
    weatherTimeFormatter.format(Instant.ofEpochSecond(epochSeconds))

@Composable
private fun DemoRideCard(
    isDemoRideActive: Boolean,
    onToggleDemoRide: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Demo ride",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                if (isDemoRideActive) {
                    "Demo ride in progress: DEMO-001"
                } else {
                    "Ready to start a demo ride."
                }
            )

            Button(
                onClick = onToggleDemoRide,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isDemoRideActive) "End demo ride"
                    else "Start demo ride"
                )
            }

            Text(
                "Demo only: this does not rent or unlock a real bicycle.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}