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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.data.EnvironmentData

@Composable
fun RideScreen(
    environment: EnvironmentData,
    temperature: Double,
    temperatureUnit: String,
    isDemoRideActive: Boolean,
    onToggleDemoRide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("RideReady", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Check your environment before a ride.",
            style = MaterialTheme.typography.bodyLarge
        )

        EnvironmentCard(
            environment = environment,
            temperature = temperature,
            temperatureUnit = temperatureUnit
        )
        DemoRideCard(
            isDemoRideActive = isDemoRideActive,
            onToggleDemoRide = onToggleDemoRide
        )
    }
}

@Composable
private fun EnvironmentCard(
    environment: EnvironmentData,
    temperature: Double,
    temperatureUnit: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Environment", style = MaterialTheme.typography.titleLarge)
            Text(
                "${environment.locationName} · Sample data",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Temperature: $temperature $temperatureUnit",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Air quality: US AQI ${environment.usAqi}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Wind speed: ${environment.windSpeedKmh} km/h",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Rain probability: ${environment.rainProbabilityPercent}%",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Sample values only. Live conditions are not connected yet.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

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
            Text("Demo ride", style = MaterialTheme.typography.titleLarge)
            Text(
                if (isDemoRideActive) "Demo ride in progress: DEMO-001"
                else "Ready to start a demo ride.",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = onToggleDemoRide,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isDemoRideActive) "End demo ride" else "Start demo ride")
            }
            Text(
                "Demo only: this does not rent or unlock a real bicycle.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}