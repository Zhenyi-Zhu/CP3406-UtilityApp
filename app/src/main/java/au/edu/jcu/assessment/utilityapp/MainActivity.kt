package au.edu.jcu.assessment.utilityapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UtilityApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    UtilityApp()
}

@Composable
fun UtilityApp() {
    val systemDarkTheme = isSystemInDarkTheme()
    var selectedTab by rememberSaveable { mutableStateOf("Utility") }
    // Shared preferences belong above both screens.
    var useFahrenheit by rememberSaveable { mutableStateOf(false) }
    var useDarkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }
    var isDemoRideActive by rememberSaveable { mutableStateOf(false) }

    UpdateSystemBarIcons(useDarkTheme = useDarkTheme)

    MobileTech_UtilityAppTheme(darkTheme = useDarkTheme) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Ride") },
                        label = { Text("Ride") },
                        selected = selectedTab == "Utility",
                        onClick = { selectedTab = "Utility" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = selectedTab == "Settings",
                        onClick = { selectedTab = "Settings" }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    "Utility" -> UtilityScreen(
                        useFahrenheit = useFahrenheit,
                        isDemoRideActive = isDemoRideActive,
                        onToggleDemoRide = { isDemoRideActive = !isDemoRideActive }
                    )
                    "Settings" -> SettingsScreen(
                        useFahrenheit = useFahrenheit,
                        useDarkTheme = useDarkTheme,
                        onUnitChange = { useFahrenheit = it },
                        onThemeChange = { useDarkTheme = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateSystemBarIcons(useDarkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            // Match the system icons to the app's selected theme.
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !useDarkTheme
            controller.isAppearanceLightNavigationBars = !useDarkTheme
        }
    }
}

@Composable
fun UtilityScreen(
    useFahrenheit: Boolean,
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

        EnvironmentCard(useFahrenheit = useFahrenheit)
        DemoRideCard(
            isDemoRideActive = isDemoRideActive,
            onToggleDemoRide = onToggleDemoRide
        )
    }
}

@Composable
private fun EnvironmentCard(useFahrenheit: Boolean) {
    val temperatureCelsius = 30.0
    val temperature = if (useFahrenheit) {
        temperatureCelsius * 9.0 / 5.0 + 32.0
    } else {
        temperatureCelsius
    }
    val temperatureUnit = if (useFahrenheit) "°F" else "°C"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Environment", style = MaterialTheme.typography.titleLarge)
            Text(
                "Singapore · Sample data",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Temperature: $temperature $temperatureUnit",
                style = MaterialTheme.typography.titleLarge
            )
            Text("Air quality: US AQI 42", style = MaterialTheme.typography.bodyLarge)
            Text("Wind speed: 12 km/h", style = MaterialTheme.typography.bodyLarge)
            Text("Rain probability: 30%", style = MaterialTheme.typography.bodyLarge)
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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
        // The parent row handles taps and provides a single accessible control.
        Switch(checked = checked, onCheckedChange = null)
    }
}
