# RideReady

RideReady is a CP3406 Assessment 1 utility app built with Kotlin, Jetpack Compose and Material Design 3. It helps cyclists check environmental information before a ride and practise a demo bike rental flow.

## Getting Started
## Current prototype

- Sample temperature, US AQI, wind speed and rain probability.
- Start and end a simulated ride using demo bicycle DEMO-001.
- Fahrenheit and dark theme switches in Settings.
- Shared preferences and ride state when switching tabs.
- Material cards for environmental information and ride controls.

Environmental values are sample data. The app does not rent or unlock a real bicycle. QR scanning, live data and permanent preference storage are planned for later development.

## How to run

1. Clone or download this repository.
2. Open the project folder in Android Studio.
3. Allow Gradle sync to finish.
4. Run on a compatible Android emulator or physical device.

## Code structure

- `MainActivity.kt`: obtains sample data and starts the Compose UI.
- `ui/RideReadyApp.kt`: shared state, theme, bottom navigation and previews.
- `ui/screens/RideScreen.kt`: environmental information and demo ride controls.
- `ui/screens/SettingsScreen.kt`: temperature and appearance preferences.
- `data/EnvironmentData.kt`: the environmental data model.
- `data/EnvironmentRepository.kt`: the repository interface and sample implementation.

## Week 2 progress

- Built Ride and Settings screens using Jetpack Compose.
- Displayed sample environmental data.
- Added start/end demo ride buttons and a temperature unit setting.

Tested on the Android emulator. Starting and ending a demo ride updated the status and button text correctly. Changing to Fahrenheit displayed 86.0 °F. Switching tabs preserved the active ride and selected unit.

## Week 3 daily development

### cards and clearer content

- Grouped environmental information and demo ride controls into separate cards.
- Extracted the cards into small composable functions.
- Used Material typography for headings and supporting text.
- Kept the screen scrollable without fixed card heights.

Testing: both cards displayed without clipped text at the tested screen size and default text size. Ride actions and temperature conversion worked correctly. Switching tabs preserved the active ride and selected unit.
Design observation: cards separate information from the ride action. Sample data and simulated rental are explicitly labelled to distinguish the prototype from live conditions and a real rental service.

### settings and theme

- Replaced the temperature unit button with a labelled Fahrenheit switch.
- Added a dark theme preference that updates both screens.
- Made each preference row tappable and assigned switch semantics.
- Adjusted system icon appearance to match the selected app theme.
- Removed outdated starter counter and placeholder descriptions from this README.

Testing: checked temperature conversion in both directions, light and dark themes on both screens, and tapping both the switches and their text rows. Settings changes preserved the active ride. Ending the ride restored the ready state.
Design observation: each setting shows its current value in text as well as the switch position. Colours follow the Material theme. The dark Ride screen displayed readable text and light status bar icons.

### larger text and small-screen checks

- Added 320 × 568 dp previews for the Ride and Settings screens.
- Checked both screens with the maximum system font size on the Android emulator.
- Checked scrolling, navigation, demo ride buttons and temperature settings.

With larger text, content wrapped correctly and remained accessible by scrolling. The demo ride buttons and Settings controls worked correctly.
In the small-screen previews, visible text stayed within the cards and both switches fitted within the Settings screen. Interactive testing confirmed that scrolling reached the demo ride buttons, starting and ending a ride updated the state, and selecting Fahrenheit displayed 86.0 °F on the Ride screen.

### Design observation

Flexible card heights and scrolling accommodated larger text and a smaller viewport. No layout fixes were needed for the configurations checked.

## Week 4 daily development

### separate data and screens

- Added an EnvironmentData model and an EnvironmentRepository interface.
- Centralised sample environmental values in SampleEnvironmentRepository.
- Moved Ride and Settings into separate screen files.
- Moved navigation, shared state, theme handling and previews into RideReadyApp.
- Simplified MainActivity to obtain sample data and start the UI.

### Testing and observation

Tested the refactored app on the Android emulator. Temperature switched correctly between 30.0 °C and 86.0 °F. Starting a demo ride, changing units and theme, and switching tabs preserved the active ride. Ending the ride restored the ready state.

### Architecture observation

The Ride screen now receives environmental data instead of defining sample values inside its cards. This gives data access a separate responsibility and prepares the app for a different data source.

Shared state still uses rememberSaveable in RideReadyApp. The next step is to review this state and introduce a ViewModel.

## Planned extensions

- Introduce a ViewModel and observable UI state.
- Add tests for state-management logic.
- Connect live environmental data.
- Add QR scanning for the simulated rental flow.
- Store preferences permanently.

## Template attribution

This project started from the lecturer's MobileTech Utility App template, provided for educational use in CP3406.