# RideReady

RideReady is a CP3406 Assessment 1 utility app built with Kotlin, Jetpack Compose and Material Design 3. It helps cyclists check environmental information before a ride and practise a demo bike rental flow.

## Current prototype

- Network weather and US AQI data for a fixed Singapore location.
- Temperature, wind speed and rain probability with the forecast period.
- Separate timestamps for weather and air quality.
- Loading indicators, manual refresh and retry controls.
- Previously loaded values retained when a weather refresh fails.
- Weather remains available when only the air quality request fails.
- Start and end a simulated ride using demo bicycle DEMO-001.
- Fahrenheit and dark theme switches in Settings.
- Shared settings and ride state managed by ViewModel and StateFlow.
- SavedStateHandle support for restoring simple settings and ride state.
- Material cards, scrollable screens and 14 RideReady unit tests.

Environmental values are model estimates and forecasts, not direct local
sensor measurements. The app uses US AQI, which differs from Singapore PSI.
The location is fixed; GPS location detection is not implemented.

The app does not rent or unlock a real bicycle. Saved-state restoration is
not permanent preference storage. QR scanning and permanent storage remain
future extensions.

## How to run the unit tests

In Android Studio, open the package marked `(test)`. Right-click each test
class and select Run:

- `RideReadyViewModelTest`: 10 tests covering loading, preferences, ride
  state, saved-value restoration, retry, failed refresh, duplicate requests
  and cancellation.
- `NetworkEnvironmentRepositoryTest`: 4 tests covering combined data,
  separate timestamps, air quality service failure, missing or invalid air
  quality values, and cancellation propagation.

These 14 RideReady tests use fake repositories or API implementations.
They do not require an emulator or contact the external services.

Both test classes passed after the air quality integration. Emulator checks
separately verified network data display, refresh, settings and demo ride
controls.

## Code structure

- `MainActivity.kt`: starts the Compose UI through RideReadyRoute.
- `ui/RideReadyApp.kt`: connects the ViewModel to the UI and contains the theme, bottom navigation and previews.
- `ui/RideReadyUiState.kt`: immutable UI state and temperature display calculations.
- `ui/RideReadyViewModel.kt`: obtains environmental data, handles user actions and exposes read-only StateFlow.
- `ui/screens/RideScreen.kt`: environmental information and demo ride controls.
- `ui/screens/SettingsScreen.kt`: temperature and appearance preferences.
- `data/EnvironmentData.kt`: the environmental data model.
- `data/EnvironmentRepository.kt`: the repository interface and sample implementation.
- `app/src/test/java/au/edu/jcu/assessment/utilityapp/RideReadyViewModelTest.kt`: local state-management tests using a fake repository.
- `data/NetworkEnvironmentRepository.kt`: requests weather and air quality concurrently, validates responses and combines the results.
- `data/network/WeatherApi.kt`: Retrofit weather endpoint.
- `data/network/WeatherResponse.kt`: serializable weather response models.
- `data/network/WeatherApiClient.kt`: weather client configuration.
- `data/network/AirQualityApi.kt`: Retrofit air quality endpoint.
- `data/network/AirQualityResponse.kt`: serializable air quality response models.
- `data/network/AirQualityApiClient.kt`: air quality client configuration.
- `app/src/test/java/au/edu/jcu/assessment/utilityapp/NetworkEnvironmentRepositoryTest.kt`: repository tests using fake API implementations.

## Week 2 progress

- Built Ride and Settings screens using Jetpack Compose.
- Displayed sample environmental data.
- Added start/end demo ride buttons and a temperature unit setting.

Tested on the Android emulator. Starting and ending a demo ride updated the status and button text correctly. Changing to Fahrenheit displayed 86.0 °F. Switching tabs preserved the active ride and selected unit.

## Week 3 daily development

### Cards and clearer content

- Grouped environmental information and demo ride controls into separate cards.
- Extracted the cards into small composable functions.
- Used Material typography for headings and supporting text.
- Kept the screen scrollable without fixed card heights.

Testing: both cards displayed without clipped text at the tested screen size and default text size. Ride actions and temperature conversion worked correctly. Switching tabs preserved the active ride and selected unit.

Design observation: cards separate information from the ride action. Sample data and simulated rental are explicitly labelled to distinguish the prototype from live conditions and a real rental service.

### Settings and theme

- Replaced the temperature unit button with a labelled Fahrenheit switch.
- Added a dark theme preference that updates both screens.
- Made each preference row tappable and assigned switch semantics.
- Adjusted system icon appearance to match the selected app theme.
- Removed outdated starter counter and placeholder descriptions from this README.

Testing: checked temperature conversion in both directions, light and dark themes on both screens, and tapping both the switches and their text rows. Settings changes preserved the active ride. Ending the ride restored the ready state.

Design observation: each setting shows its current value in text as well as the switch position. Colours follow the Material theme. The dark Ride screen displayed readable text and light status bar icons.

### Larger text and small-screen checks

- Added 320 × 568 dp previews for the Ride and Settings screens.
- Checked both screens with the maximum system font size on the Android emulator.
- Checked scrolling, navigation, demo ride buttons and temperature settings.

With larger text, content wrapped correctly and remained accessible by scrolling. The demo ride buttons and Settings controls worked correctly.

In the small-screen previews, visible text stayed within the cards and both switches fitted within the Settings screen. Interactive testing confirmed that scrolling reached the demo ride buttons, starting and ending a ride updated the state, and selecting Fahrenheit displayed 86.0 °F on the Ride screen.

Design observation: flexible card heights and scrolling accommodated larger text and a smaller viewport. No layout fixes were needed for the configurations checked.

## Week 4 daily development

### separate data and screens

- Added an EnvironmentData model and an EnvironmentRepository interface.
- Centralised sample environmental values in SampleEnvironmentRepository.
- Moved Ride and Settings into separate screen files.
- Moved navigation, shared state, theme handling and previews into RideReadyApp.
- Simplified MainActivity to obtain sample data and start the UI.

Testing: temperature switched correctly between 30.0 °C and 86.0 °F. Starting a demo ride, changing units and theme, and switching tabs preserved the active ride. Ending the ride restored the ready state.

Architecture observation: the Ride screen receives environmental data instead of defining sample values inside its cards. This separates data access from presentation and prepares the app for a different data source.

### ViewModel, observable state and tests

- Added RideReadyUiState and RideReadyViewModel.
- Moved shared settings and ride state from composable functions into the ViewModel.
- Exposed read-only StateFlow and collected it with collectAsStateWithLifecycle.
- Passed the repository into the ViewModel through its constructor.
- Used SavedStateHandle for the temperature preference, theme preference and demo ride status.
- Kept the selected tab as local UI state using rememberSaveable.
- Moved temperature display calculations into RideReadyUiState.
- Kept previews independent of the ViewModel.
- Added six local unit tests using a fake environment repository.

### Testing and observation

The refactored app ran successfully on the Android emulator. RideReadyViewModelTest completed with six tests passed and BUILD SUCCESSFUL.

The tests checked repository-provided initial data, starting and ending a ride, temperature conversion in both directions, independent theme changes, the initial theme preference, and restoring saved values into a new ViewModel. Test data used 20.0 °C and 68.0 °F to check that conversion was not hard-coded to the prototype's 30.0 °C sample.

The restoration test rebuilt a SavedStateHandle from saved values and supplied fresh environmental data. It verified restoration logic; it did not simulate Android process death or test permanent storage.

### Architecture observation

The UI displays state and forwards user actions to the ViewModel. The ViewModel updates state and obtains data through EnvironmentRepository. Constructor injection allows the tests to supply controlled data without changing production code.

This adds some setup code, but separates state-management logic from screen layout and makes it independently testable. A future live-data repository can use the same boundary, although asynchronous loading and error states will also be needed.

ViewModel retains state across configuration changes. SavedStateHandle supports restoration when Android recreates the app with saved state. Permanent preferences still require a separate storage solution.

### Question for lecturer feedback

Is keeping the selected tab in rememberSaveable while managing shared settings and ride state in the ViewModel an appropriate separation of responsibilities for this prototype?

## Week 5 daily development

### Network weather integration

- Connected Open-Meteo weather data for a fixed Singapore location using Retrofit and Kotlin serialization.
- Added loading indicators, refresh controls and retry on failure.
- Preserved previously loaded weather when a refresh failed.
- Displayed weather timestamps, the rain forecast period and source attribution.
- Fixed a startup crash caused by a missing INTERNET permission in AndroidManifest.xml.

### Asynchronous state tests

- Updated the fake repository to support suspend functions.
- Added coroutine test utilities to control asynchronous loading.
- Expanded RideReadyViewModelTest from six to ten tests.
- Tested loading, successful retry, failed refresh, cancellation and duplicate refresh prevention.
- Checked that responses preserve settings and ride actions made while a request is pending.

Testing: all ten ViewModel tests passed with BUILD SUCCESSFUL.

Manual checks covered weather refresh, Fahrenheit and dark theme settings,
demo ride state, connection failure and retry after restoring connectivity.
On failure, the app displayed an error and a Retry button while retaining
previous weather values and user settings.

### Air quality integration

- Connected the Open-Meteo Air Quality API using the CAMS global model.
- Requested weather and air quality concurrently for the same Singapore coordinates.
- Added US AQI values and a separate air quality timestamp.
- Added CAMS and Open-Meteo attribution.
- Updated the refresh control to refresh both services.
- Displayed unavailable air quality with a retry prompt when its request failed.
- Preserved successful weather data during an air quality failure.
- Added four repository tests using fake API implementations.

Testing: all four repository tests passed. The ten ViewModel tests also
passed after integration. On the emulator, weather and US AQI displayed
with separate timestamps. Refresh, Fahrenheit conversion, theme switching
and demo ride controls worked during the manual checks.

The repository tests confirmed that an air quality failure does not discard
successful weather data. Missing or invalid AQI values remain unavailable
rather than being replaced with zero. Cancellation is propagated.

Design observation: the two services can report values for different times,
so each timestamp is displayed separately. Explicit unavailable states avoid
presenting missing air quality information as a favourable reading.

Current limitation: weather is still the primary result. If the weather
request fails, the app retains previously loaded data when available; it
does not independently display a new AQI result.

### Design observation

Previously loaded weather remains useful during a connection failure, but
must not appear to be freshly updated. The screen therefore keeps the data
timestamp visible and explicitly labels previously loaded values.

The unit tests use a fake repository to check state transitions independently
of the external service. Emulator checks cover the actual network and UI flow.

## Planned extensions

- Add a concise cycling environment summary with clearly explained conditions.
- Review the completed layout with larger text and smaller screens.
- Add QR scanning for the simulated rental flow in future development.
- Store preferences permanently in future development.

## Template attribution

This project started from the lecturer's MobileTech Utility App template, provided for educational use in CP3406.