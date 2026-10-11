package au.edu.jcu.assessment.utilityapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    useFahrenheit: Boolean,
    useDarkTheme: Boolean,
    onUnitChange: (Boolean) -> Unit,
    onThemeChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Choose how RideReady looks and displays temperature.",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    "Preferences",
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
                PreferenceSwitchRow(
                    title = "Use Fahrenheit",
                    description = if (useFahrenheit) {
                        "Current unit: Fahrenheit (°F)"
                    } else {
                        "Current unit: Celsius (°C)"
                    },
                    checked = useFahrenheit,
                    onCheckedChange = onUnitChange
                )
                PreferenceSwitchRow(
                    title = "Dark theme",
                    description = if (useDarkTheme) {
                        "Current appearance: Dark"
                    } else {
                        "Current appearance: Light"
                    },
                    checked = useDarkTheme,
                    onCheckedChange = onThemeChange
                )
            }
        }

        Text(
            "Changes apply immediately. Return to Ride to view your preferences.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PreferenceSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }

        // The row handles taps and provides a single accessible control.
        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}

