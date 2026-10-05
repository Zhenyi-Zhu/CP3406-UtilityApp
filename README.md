# RideReady
## RideReady is a CP3406 Assessment 1 utility app built with Kotlin, Jetpack Compose and Material Design 3. It helps cyclists check environmental information before a ride and practise a demo bike rental flow.
   
## Getting Started
### Current prototype
- Ride screen with sample temperature, US AQI, wind speed and rain probability.
- Start and end a simulated ride using the demo bicycle DEMO-001.
- Settings screen with a Celsius/Fahrenheit preference.
- Shared state keeps the selected unit and active ride when switching tabs.
  Environmental values are sample data. The app does not rent or unlock a
  real bicycle. QR scanning, live environmental data and permanent storage
  are planned for later development.

### How to run
1. Clone or download this repository.
2. Open the project folder in Android Studio.
3. Allow Gradle sync to finish.
4. Run the app on a compatible Android emulator or physical device.
---

## Composables
### UtilityApp()
- Contains the screen layout using a Scaffold
- Toggles content between Utility and Settings

### UtilityScreen()
- Displays a simple counter (replace with your utility logic)  
- Includes a button to increment the counter

### SettingsScreen()
- Placeholder for user preferences or configuration  
- Can be extended to modify main screen behavior (e.g., theme, units, limits)  

---

## Key Concepts Covered

| Week | Concept                        | Used In                          |
|------|--------------------------------|----------------------------------|
| 1    | Kotlin + Android Studio         | MainActivity.kt |
| 2    | Jetpack Compose Layouts         | UtilityApp(), UtilityScreen(), SettingsScreen()   |
| 3    | Material Design 3               | MobileTechUtilityAppTheme, MaterialTheme.typography |
| 4    | ViewModel | Not included in starter          |
| 5    | Retrofit  | Not included in starter          |

---

## Suggested Extensions
- Replace counter with a real utility (e.g., hydration tracker, timer)  
- Add a ViewModel for state management  
- Use SharedPreferences or DataStore to persist settings  
- Add a simple API call using Retrofit (e.g., fetch weather or quotes)  

---

## 📚 License
This template is provided for educational use in CP3406.  
Feel free to modify and extend it for your assessment.

## Week 2 progress — 4 October 2026

- Built Ride and Settings screens using Jetpack Compose.
- Displayed sample temperature, air quality, wind and rain data.
- Added buttons to start and end a demo ride.
- Added a Celsius/Fahrenheit setting that updates the Ride screen.

### Testing and observation

Tested on the Android emulator. Starting and ending a demo ride updated the status and button text correctly. Changing to Fahrenheit displayed 86.0 °F. Switching tabs preserved the active ride and selected unit.

## Week 3 — daily development

### 5 October: cards and clearer content

- Grouped environment information and demo ride controls into separate cards.
- Extracted the two cards into small composable functions.
- Used Material typography to distinguish headings and supporting text.
- Kept the screen scrollable without fixed card heights.
- Updated this README to describe RideReady rather than the starter counter.

### Testing and observation

Checked the revised layout on the Android emulator. Both cards displayed without clipped text at the tested screen size and default text size. Starting a demo ride changed the status and button label. Selecting Fahrenheit in Settings displayed 86.0 °F on the Ride screen and preserved the active ride. Ending the demo ride restored the ready state.

### Design observation

The cards separate environmental information from the demo ride action. Sample data and simulated rental are labelled explicitly so users can distinguish the prototype from live conditions and a real rental service. Larger text, smaller screens and theme checks are planned for the next two days.

### Plan for 6 October

Improve the Settings screen and review the light and dark theme.

### Plan for 7 October

Check larger text, a smaller screen, navigation and button behaviour. Record observations and any fixes.

## Template attribution

This project started from the lecturer's MobileTech Utility App template, provided for educational use in CP3406.
