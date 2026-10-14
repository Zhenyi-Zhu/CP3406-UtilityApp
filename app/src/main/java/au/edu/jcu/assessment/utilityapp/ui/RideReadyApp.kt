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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.data.SampleEnvironmentRepository
import au.edu.jcu.assessment.utilityapp.ui.screens.RideScreen
import au.edu.jcu.assessment.utilityapp.ui.screens.SettingsScreen
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

@Composable
fun RideReadyRoute() {
    val systemDarkTheme = isSystemInDarkTheme()
    val factory = remember(systemDarkTheme) {
        RideReadyViewModel.factory(
            repository = SampleEnvironmentRepository(),
            initialDarkTheme = systemDarkTheme
        )
    }
    val rideReadyViewModel: RideReadyViewModel = viewModel(factory = factory)
    val uiState by rideReadyViewModel.uiState.collectAsStateWithLifecycle()

    RideReadyApp(
        uiState = uiState,
        onUnitChange = rideReadyViewModel::setUseFahrenheit,
        onThemeChange = rideReadyViewModel::setUseDarkTheme,
        onToggleDemoRide = rideReadyViewModel::toggleDemoRide
    )
}

@Composable
fun RideReadyApp(
    uiState: RideReadyUiState,
    onUnitChange: (Boolean) -> Unit,
    onThemeChange: (Boolean) -> Unit,
    onToggleDemoRide: () -> Unit,
    initialTab: String = "Utility"
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }

    UpdateSystemBarIcons(useDarkTheme = uiState.useDarkTheme)

    MobileTech_UtilityAppTheme(darkTheme = uiState.useDarkTheme) {
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
                    "Utility" -> RideScreen(
                        environment = uiState.environment,
                        temperature = uiState.temperature,
                        temperatureUnit = uiState.temperatureUnit,
                        isDemoRideActive = uiState.isDemoRideActive,
                        onToggleDemoRide = onToggleDemoRide
                    )
                    "Settings" -> SettingsScreen(
                        useFahrenheit = uiState.useFahrenheit,
                        useDarkTheme = uiState.useDarkTheme,
                        onUnitChange = onUnitChange,
                        onThemeChange = onThemeChange
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
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !useDarkTheme
            controller.isAppearanceLightNavigationBars = !useDarkTheme
        }
    }
}

@Composable
private fun RideReadyPreviewContent(initialTab: String = "Utility") {
    val systemDarkTheme = isSystemInDarkTheme()
    var uiState by remember {
        mutableStateOf(
            RideReadyUiState(
                environment = SampleEnvironmentRepository().getEnvironment(),
                useDarkTheme = systemDarkTheme
            )
        )
    }

    RideReadyApp(
        uiState = uiState,
        onUnitChange = { uiState = uiState.copy(useFahrenheit = it) },
        onThemeChange = { uiState = uiState.copy(useDarkTheme = it) },
        onToggleDemoRide = {
            uiState = uiState.copy(isDemoRideActive = !uiState.isDemoRideActive)
        },
        initialTab = initialTab
    )
}

@Preview(showBackground = true)
@Composable
fun RideReadyAppPreview() {
    RideReadyPreviewContent()
}

@Preview(
    name = "Ride - small phone",
    showBackground = true,
    widthDp = 320,
    heightDp = 568
)
@Composable
fun RideSmallScreenPreview() {
    RideReadyPreviewContent()
}

@Preview(
    name = "Settings - small phone",
    showBackground = true,
    widthDp = 320,
    heightDp = 568
)
@Composable
fun SettingsSmallScreenPreview() {
    RideReadyPreviewContent(initialTab = "Settings")
}