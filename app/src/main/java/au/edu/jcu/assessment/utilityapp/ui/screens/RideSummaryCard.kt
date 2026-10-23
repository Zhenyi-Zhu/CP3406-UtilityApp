package au.edu.jcu.assessment.utilityapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.RideReadyUiState
import au.edu.jcu.assessment.utilityapp.ui.buildRideSummary

@Composable
fun RideSummaryCard(uiState: RideReadyUiState) {
    val summary = buildRideSummary(uiState)
    val uriHandler = LocalUriHandler.current
    var showExplanation by rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Before you ride",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                summary.notice,
                style = MaterialTheme.typography.bodySmall
            )

            summary.items.forEach { item ->
                Text(
                    item,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TextButton(
                onClick = {
                    showExplanation = !showExplanation
                }
            ) {
                Text(
                    if (showExplanation) "Hide explanation"
                    else "About these reminders"
                )
            }

            if (showExplanation) {
                Text(
                    "Weather reminders use prototype planning thresholds: " +
                            "temperature at least 30 °C (86 °F), rain probability " +
                            "at least 50%, or wind at least 20 km/h. " +
                            "These are not official warning levels.",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    "Rain refers to the forecast period shown below. " +
                            "AQI labels follow US AQI categories. " +
                            "Check local alerts and conditions before departure.",
                    style = MaterialTheme.typography.bodySmall
                )

                TextButton(
                    onClick = {
                        uriHandler.openUri(
                            "https://www.airnow.gov/aqi/aqi-basics/"
                        )
                    }
                ) {
                    Text("US AQI category guide")
                }
            }
        }
    }
}