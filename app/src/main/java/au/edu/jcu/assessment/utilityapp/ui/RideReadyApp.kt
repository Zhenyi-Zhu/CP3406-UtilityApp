package au.edu.jcu.assessment.utilityapp.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import au.edu.jcu.assessment.utilityapp.data.EnvironmentData
import au.edu.jcu.assessment.utilityapp.data.SampleEnvironmentRepository
import au.edu.jcu.assessment.utilityapp.ui.screens.RideScreen
import au.edu.jcu.assessment.utilityapp.ui.screens.SettingsScreen
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

@Preview(showBackground = true)
@Composable
fun RideReadyAppPreview() {
    RideReadyApp(
        environment = SampleEnvironmentRepository().getEnvironment()
    )
}

@Preview(
    name = "Ride - small phone",
    showBackground = true,
    widthDp = 320,
    heightDp = 568
)
@Composable
fun RideSmallScreenPreview() {
    RideReadyApp(
        environment = SampleEnvironmentRepository().getEnvironment()
    )
}

@Preview(
    name = "Settings - small phone",
    showBackground = true,
    widthDp = 320,
    heightDp = 568
)
@Composable
fun SettingsSmallScreenPreview() {
    RideReadyApp(
        environment = SampleEnvironmentRepository().getEnvironment(),
        initialTab = "Settings"
    )
}

@Composable
fun RideReadyApp(
    environment: EnvironmentData,
    initialTab: String = "Utility"
) {
    val systemDarkTheme = isSystemInDarkTheme()
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
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
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Ride"
                            )
                        },
                        label = { Text("Ride") },
                        selected = selectedTab == "Utility",
                        onClick = { selectedTab = "Utility" }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") },
                        selected = selectedTab == "Settings",
                        onClick = { selectedTab = "Settings" }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    "Utility" -> RideScreen(
                        environment = environment,
                        useFahrenheit = useFahrenheit,
                        isDemoRideActive = isDemoRideActive,
                        onToggleDemoRide = {
                            isDemoRideActive = !isDemoRideActive
                        }
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
            val window = (view.context as? Activity)?.window
                ?: return@SideEffect

            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !useDarkTheme
            controller.isAppearanceLightNavigationBars = !useDarkTheme
        }
    }
}

