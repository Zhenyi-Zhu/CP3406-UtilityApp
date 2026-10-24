# RideReady

RideReady is a CP3406 Assessment 1 utility app built with Kotlin, Jetpack Compose and Material Design 3. It helps cyclists check environmental information before a ride and practise a demo bike rental flow.

## Current prototype

- Network weather and US AQI data for a fixed Singapore location.
- Before-you-ride summary with temperature, rain, wind and air quality information.
- Expandable explanation of reminder thresholds and a link to the US AQI category guide.
- Temperature, wind speed and rain probability with the forecast period.
- Separate timestamps for weather and air quality.
- Loading indicators, manual refresh and retry controls.
- Previously loaded values retained and labelled when a weather refresh fails.
- Weather remains available when only the air quality request fails.
- Start and end a simulated ride using demo bicycle DEMO-001.
- Fahrenheit and dark theme switches in Settings.
- Shared settings and ride state managed by ViewModel and StateFlow.
- SavedStateHandle support for restoring simple settings and ride state.
- Material cards and scrollable screens.
- Twenty RideReady unit tests covering state management, repository behaviour and summary logic.

Environmental values are model estimates and forecasts, not direct local sensor measurements. The app uses US AQI, which differs from Singapore PSI. The location is fixed; GPS location detection is not implemented.

The app does not rent or unlock a real bicycle. Saved-state restoration is not permanent preference storage. QR scanning and permanent storage remain future extensions.

## How to run

1. Clone or download this repository.
2. Open the project root folder in Android Studio.
3. Allow Gradle sync and any required SDK downloads to finish.
4. Select an emulator or physical device running Android 8.0 / API 26 or later.
5. Run the app module.
6. Connect the device to the internet to load environmental data.

The app declares the INTERNET permission in AndroidManifest.xml. The current API configuration does not require an API key.

The Ride screen loads environmental data automatically. Use Refresh environment to request updated values, or Retry after a failure. Open Settings to change the temperature unit or theme.

Compose previews use sample data and do not make network requests.

## How to run the unit tests

In Android Studio, open the package marked `(test)`. Right-click each test class and select Run:

- `RideReadyViewModelTest`: 10 tests covering loading, preferences, ride state, saved-value restoration, retry, failed refresh, duplicate requests and cancellation.
- `NetworkEnvironmentRepositoryTest`: 4 tests covering combined data, separate timestamps, air quality service failure, missing or invalid air quality values, and cancellation propagation.
- `RideSummaryTest`: 6 tests covering AQI category boundaries, reminder thresholds in both temperature units, missing data, loading and unavailable states, previously loaded values, and sample-data labelling.

These 20 RideReady tests use controlled data, fake repositories or fake API implementations. They do not require an emulator or contact the external services.

The ten ViewModel tests and four repository tests passed after the air quality integration. The six summary tests passed after the summary feature was added.

Unit tests check application logic. Emulator checks separately cover network access, rendering and user interaction. The development notes below record the checks completed at each stage.

## Before-you-ride summary

The summary appears above the detailed environmental readings and demo ride controls.

It uses the following prototype planning thresholds:

| Input | Reminder threshold | Reminder |
| --- | --- | --- |
| Temperature | At least 30 °C / 86 °F | Bring water and plan breaks. |
| Rain probability | At least 50% | Plan for wet conditions. |
| Wind speed | At least 20 km/h | Consider exposed parts of the route. |

These thresholds are prototype design choices, not official weather warning levels. Temperature reminders use the Celsius value internally, so changing the display unit does not change the decision.

Air quality labels follow US AQI categories:

| US AQI | Category |
| --- | --- |
| 0–50 | Good |
| 51–100 | Moderate |
| 101–150 | Unhealthy for sensitive groups |
| 151–200 | Unhealthy |
| 201–300 | Very unhealthy |
| 301 and above | Hazardous |

Missing information is labelled unavailable rather than replaced with zero. During a refresh or after a failed refresh, the summary identifies when it is using previously loaded values.

The About these reminders control explains the thresholds and provides a link to the US AQI category guide. Users should check the separate timestamps, local alerts and actual conditions before departure. The summary does not provide a guarantee that riding is safe.

## Architecture and state

The Compose UI displays state and forwards user actions to RideReadyViewModel. The ViewModel obtains environmental data through EnvironmentRepository and exposes immutable RideReadyUiState through a read-only StateFlow.

NetworkEnvironmentRepository requests weather and air quality concurrently, validates the responses and combines the available results. Constructor injection allows tests to replace external services with controlled implementations.

The UI collects state using collectAsStateWithLifecycle. Shared preferences and demo ride state belong to the ViewModel. The selected tab and summary explanation visibility remain local UI state using rememberSaveable.

SavedStateHandle supports restoration of simple settings and ride state when Android recreates the app with saved state. It does not provide permanent preference storage.

Summary text and category decisions are kept in RideSummary.kt, separately from the Compose card, so the logic can be tested without rendering the UI.

## Code structure

Production Kotlin paths below are relative to `app/src/main/java/au/edu/jcu/assessment/utilityapp/`.

- `MainActivity.kt`: starts the Compose UI through RideReadyRoute.
- `ui/RideReadyApp.kt`: connects the ViewModel to the UI and contains the theme, bottom navigation and previews.
- `ui/RideReadyUiState.kt`: immutable UI state and temperature display calculations.
- `ui/RideReadyViewModel.kt`: obtains environmental data, handles user actions and exposes read-only StateFlow.
- `ui/RideSummary.kt`: summary text, reminder thresholds and US AQI categories.
- `ui/screens/RideScreen.kt`: combines the summary, environmental information and demo ride controls.
- `ui/screens/RideSummaryCard.kt`: summary card and expandable explanation.
- `ui/screens/SettingsScreen.kt`: temperature and appearance preferences.
- `data/EnvironmentData.kt`: the environmental data model.
- `data/EnvironmentRepository.kt`: the repository interface and sample implementation.
- `data/NetworkEnvironmentRepository.kt`: concurrent requests, response validation and combined environmental data.
- `data/network/WeatherApi.kt`: Retrofit weather endpoint.
- `data/network/WeatherResponse.kt`: serializable weather response models.
- `data/network/WeatherApiClient.kt`: weather client configuration.
- `data/network/AirQualityApi.kt`: Retrofit air quality endpoint.
- `data/network/AirQualityResponse.kt`: serializable air quality response models.
- `data/network/AirQualityApiClient.kt`: air quality client configuration.

Local unit tests are under `app/src/test/java/au/edu/jcu/assessment/utilityapp/`.

- `RideReadyViewModelTest.kt`: state-management tests using a fake repository.
- `NetworkEnvironmentRepositoryTest.kt`: repository tests using fake API implementations.
- `RideSummaryTest.kt`: summary and category tests using fixed environmental data.

## Development log

### 4 October 2026

- Built Ride and Settings screens using Jetpack Compose.
- Displayed sample temperature, air quality, wind and rain data.
- Added buttons to start and end a demo ride.
- Added a Celsius/Fahrenheit setting that updates the Ride screen.

Testing: starting and ending a demo ride updated the status and button text correctly on the Android emulator. Changing to Fahrenheit displayed 86.0 °F. Switching tabs preserved the active ride and selected unit.

### 5 October 2026

- Grouped environmental information and demo ride controls into separate cards.
- Extracted the cards into small composable functions.
- Used Material typography to distinguish headings and supporting text.
- Kept the screen scrollable without fixed card heights.
- Updated this README to describe RideReady rather than the starter counter.

Testing: both cards displayed without clipped text at the tested screen size and default text size. Starting a demo ride changed the status and button label. Selecting Fahrenheit displayed 86.0 °F and preserved the active ride. Ending the ride restored the ready state.

Design observation: the cards separate environmental information from the demo ride action. Sample data and simulated rental are labelled explicitly so users can distinguish the prototype from live conditions and a real rental service.

### 6 October 2026

- Replaced the temperature unit button with a labelled Fahrenheit switch.
- Added a dark theme preference that updates both screens.
- Made each preference row tappable and assigned switch semantics.
- Adjusted system icon appearance to match the selected app theme.
- Removed outdated starter counter and placeholder descriptions from this README.

Testing: checked temperature conversion in both directions, light and dark themes on both screens, and tapping both the switches and their text rows. Settings changes preserved the active ride. Ending the ride restored the ready state.

Design observation: each setting shows its current value in text as well as the switch position. Colours follow the Material theme. The dark Ride screen displayed readable text and light status bar icons.

### 7 October 2026

- Added 320 × 568 dp previews for the Ride and Settings screens.
- Checked both screens with the maximum system font size on the Android emulator.
- Checked scrolling, navigation, demo ride buttons and temperature settings.

Testing: with larger text, content wrapped correctly and remained accessible by scrolling. The demo ride buttons and Settings controls worked correctly.

In the small-screen previews, visible text stayed within the cards and both switches fitted within the Settings screen. Interactive testing confirmed that scrolling reached the demo ride buttons, starting and ending a ride updated the state, and selecting Fahrenheit displayed 86.0 °F on the Ride screen.

Design observation: flexible card heights and scrolling accommodated larger text and a smaller viewport. No layout fixes were needed for the configurations checked.

These observations describe the layout tested on this date. Later additions require their own layout checks.

### 11 October 2026

- Added an EnvironmentData model and an EnvironmentRepository interface.
- Centralised sample environmental values in SampleEnvironmentRepository.
- Moved Ride and Settings into separate screen files.
- Moved navigation, shared state, theme handling and previews into RideReadyApp.
- Simplified MainActivity to obtain sample data and start the UI.

Testing: the refactored app ran on the Android emulator. Temperature switched correctly between 30.0 °C and 86.0 °F. Starting a demo ride, changing units and theme, and switching tabs preserved the active ride. Ending the ride restored the ready state.

Architecture observation: the Ride screen receives environmental data instead of defining sample values inside its cards. This separates data access from presentation and prepares the app for a different data source.

At this stage, shared state still used rememberSaveable in RideReadyApp. Moving shared state into a ViewModel was the next development step.

### 14 October 2026

- Added RideReadyUiState and RideReadyViewModel.
- Moved shared settings and ride state from composable functions into the ViewModel.
- Exposed read-only StateFlow and collected it with collectAsStateWithLifecycle.
- Passed the repository into the ViewModel through its constructor.
- Used SavedStateHandle for the temperature preference, theme preference and demo ride status.
- Kept the selected tab as local UI state using rememberSaveable.
- Moved temperature display calculations into RideReadyUiState.
- Kept previews independent of the ViewModel.
- Added six local unit tests using a fake environment repository.

Testing: the refactored app ran successfully on the Android emulator. RideReadyViewModelTest completed with six tests passed and BUILD SUCCESSFUL.

The tests checked repository-provided initial data, starting and ending a ride, temperature conversion in both directions, independent theme changes, the initial theme preference, and restoring saved values into a new ViewModel. Test data used 20.0 °C and 68.0 °F to check that conversion was not hard-coded to the prototype's 30.0 °C sample.

The restoration test rebuilt a SavedStateHandle from saved values and supplied fresh environmental data. It verified restoration logic; it did not simulate Android process death or test permanent storage.

Architecture observation: the UI displays state and forwards user actions to the ViewModel. The ViewModel updates state and obtains data through EnvironmentRepository. Constructor injection allows tests to supply controlled data without changing production code.

This adds some setup code, but separates state-management logic from screen layout and makes it independently testable. At this stage, asynchronous loading and error states were still needed before adding a network repository.

ViewModel retains state across configuration changes. SavedStateHandle supports restoration when Android recreates the app with saved state. Permanent preferences still require a separate storage solution.

Question for lecturer feedback: is keeping the selected tab in rememberSaveable while managing shared settings and ride state in the ViewModel an appropriate separation of responsibilities for this prototype?

### 21 October 2026

- Connected Open-Meteo weather data for a fixed Singapore location using Retrofit and Kotlin serialization.
- Added loading indicators, refresh controls and retry on failure.
- Preserved previously loaded weather when a refresh failed.
- Displayed weather timestamps, the rain forecast period and source attribution.
- Fixed a startup crash caused by a missing INTERNET permission in AndroidManifest.xml.

Testing and debugging: the initial network request caused a SecurityException because the app did not have the INTERNET permission. After adding the permission and running the app again, weather data displayed on the emulator.

Design observation: previously loaded weather remains useful during a connection failure, but must not appear to be freshly updated. The screen therefore keeps the data timestamp visible and explicitly labels previously loaded values.

### 22 October 2026

- Updated the fake repository to support suspend functions.
- Added coroutine test utilities to control asynchronous loading.
- Expanded RideReadyViewModelTest from six to ten tests.
- Tested loading, successful retry, failed refresh, cancellation and duplicate refresh prevention.
- Checked that responses preserve settings and ride actions made while a request is pending.
- Connected the Open-Meteo Air Quality API using the CAMS global model.
- Requested weather and air quality concurrently for the same Singapore coordinates.
- Added US AQI values and a separate air quality timestamp.
- Added CAMS and Open-Meteo attribution.
- Updated the refresh control to refresh both services.
- Displayed unavailable air quality with a retry prompt when its request failed.
- Preserved successful weather data during an air quality failure.
- Added four repository tests using fake API implementations.

Testing: all ten ViewModel tests and all four repository tests passed.

Manual checks covered weather refresh, Fahrenheit and dark theme settings, demo ride state, connection failure and retry after restoring connectivity. On failure, the app displayed an error and a Retry button while retaining previous weather values and user settings.

After air quality integration, weather and US AQI displayed with separate timestamps on the emulator. Refresh, Fahrenheit conversion, theme switching and demo ride controls worked during the manual checks.

The repository tests confirmed that an air quality failure does not discard successful weather data. Missing or invalid AQI values remain unavailable rather than being replaced with zero. Cancellation is propagated.

Design observation: the two services can report values for different times, so each timestamp is displayed separately. Explicit unavailable states avoid presenting missing air quality information as a favourable reading.

Current limitation: weather is still the primary result. If the weather request fails, the app retains previously loaded data when available; it does not independently display a new AQI result.

### 23 October 2026

- Added a Before you ride card that summarises temperature, rain probability, wind and US AQI.
- Separated summary logic from the Compose card so it can be tested independently.
- Added an expandable explanation of the prototype reminder thresholds and a link to the US AQI category guide.
- Kept temperature reminders consistent when switching between Celsius and Fahrenheit.
- Labelled missing data, sample data and previously loaded values explicitly.
- Added six summary logic tests.
- Updated the README with the summary behaviour, test coverage, architecture, data sources and limitations.

Testing: all six RideSummaryTest tests passed. They cover both sides of AQI category boundaries, reminder thresholds in both temperature units, missing optional values, initial loading and unavailable data, previously loaded values during refresh or failure, and sample-data labelling.

Together with the existing ten ViewModel tests and four repository tests, the project now contains twenty RideReady unit tests.

The summary card displayed above the detailed environmental readings in the emulator. At the observed screen size in the light theme, its text was visible without clipping and its values matched the detailed readings.

### 24 October 2026

- Reviewed Ride and Settings in light and dark themes.
- Checked the summary, expanded explanation, environmental details and demo ride controls.
- Opened the AirNow AQI guide from the summary card.
- Checked both screens with the emulator font size set to maximum.
- Added screenshots documenting standard and enlarged text layouts in both themes.

Testing and observation: the summary and detailed readings displayed matching environmental values. The observed 28.4 °C reading displayed as 83.1 °F after changing the temperature unit. Screenshots captured both the ready and active demo ride states.

With enlarged text, summary content, timestamps and source labels wrapped within their cards. Scrolling reached the refresh and demo ride controls. Settings labels wrapped without overlapping either switch in both light and dark themes. Bottom navigation remained visible.

The reminder explanation expanded successfully, and its AQI guide link opened in the browser.

No layout changes were required for the configurations inspected. These checks cover the Medium Phone emulator used for testing; they do not establish compatibility with every screen size or accessibility configuration.

## Interface screenshots

Screenshots captured on 24 October 2026 show RideReady on the Medium Phone emulator with standard and enlarged text.

### Standard text

- [Ride screen in light theme](ride-light.png)
- [Ride screen in dark theme with Fahrenheit](ride-dark.png)
- [Active demo ride](demo-ride-active.png)
- [Expanded reminder explanation](summary-expanded.png)

### Enlarged text

- [Ride screen in light theme](ride-large-light.png)
- [Ride screen in dark theme](ride-large-dark.png)
- [Refresh and demo ride controls](ride-large-controls.png)
- [Settings in light theme](settings-large-light.png)
- [Settings in dark theme](settings-large-dark.png)

## Data sources and attribution

- Weather data: [Open-Meteo Weather API](https://open-meteo.com/en/docs).
- Air quality data: CAMS global model through the [Open-Meteo Air Quality API](https://open-meteo.com/en/docs/air-quality-api).
- US AQI category reference: [AirNow AQI Basics](https://www.airnow.gov/aqi/aqi-basics/).

Weather and air quality may refer to different times. The app displays separate timestamps in Singapore time rather than implying that all values were measured simultaneously.

Rain probability applies to the forecast period shown on the Ride screen.

SampleEnvironmentRepository supplies clearly labelled sample data for previews and development.

## Current limitations

- The location is fixed to Singapore; there is no GPS or location search.
- Environmental values depend on external services and are model estimates or forecasts.
- Weather remains the primary result when combining the two services.
- Previously loaded values can remain visible after a failed refresh; their timestamps and status should be checked.
- Weather reminder thresholds are simple prototype rules.
- Demo rides do not rent, reserve or unlock a real bicycle.
- QR scanning is not implemented.
- Preferences are not stored permanently.
- Saved-value unit tests do not simulate a complete Android process-death scenario.

## Remaining checks and future extensions

- Check the final layout on a smaller screen; large-font checks on the current emulator are recorded above.
- Complete final build, interaction and documentation checks before submission.
- Consider QR scanning for the simulated rental flow in future development.
- Consider permanent preference storage in future development.

## Template attribution

This project started from the lecturer's MobileTech Utility App template, provided for educational use in CP3406.