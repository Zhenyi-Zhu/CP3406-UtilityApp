package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTech_UtilityAppTheme {
                UtilityApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    MobileTech_UtilityAppTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp() {
    var selectedTab by rememberSaveable { mutableStateOf("Utility") }
    // Both screens use this setting, so its state belongs in their parent.
    var useFahrenheit by rememberSaveable { mutableStateOf(false) }
    var isDemoRideActive by rememberSaveable { mutableStateOf(false) }

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
                    onUnitChange = { useFahrenheit = it }
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    useFahrenheit: Boolean,
    isDemoRideActive: Boolean,
    onToggleDemoRide: () -> Unit
) {
    val temperatureCelsius = 30.0
    val temperature = if (useFahrenheit) {
        temperatureCelsius * 9.0 / 5.0 + 32.0
    } else {
        temperatureCelsius
    }
    val temperatureUnit = if (useFahrenheit) "°F" else "°C"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("RideReady", style = MaterialTheme.typography.headlineMedium)
        Text("Check your environment before a ride.")
        Text("Singapore · Sample environment data")
        Text("Temperature: $temperature $temperatureUnit")
        Text("Air quality: US AQI 42 (sample)")
        Text("Wind speed: 12 km/h (sample)")
        Text("Rain probability: 30% (sample)")

        Text("Demo ride", style = MaterialTheme.typography.titleLarge)
        Text(
            if (isDemoRideActive) "Demo ride in progress: DEMO-001"
            else "Ready to start a demo ride."
        )

        Button(onClick = onToggleDemoRide) {
            Text(if (isDemoRideActive) "End demo ride" else "Start demo ride")
        }
        Text("Demo only: this does not rent or unlock a real bicycle.")
    }
}

@Composable
fun SettingsScreen(
    useFahrenheit: Boolean,
    onUnitChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Temperature unit", style = MaterialTheme.typography.titleLarge)
        Text(if (useFahrenheit) "Current unit: Fahrenheit (°F)" else "Current unit: Celsius (°C)")
        Button(onClick = { onUnitChange(!useFahrenheit) }) {
            Text(if (useFahrenheit) "Use Celsius" else "Use Fahrenheit")
        }
        Text("Return to Ride to see the temperature in your selected unit.")
    }
}
