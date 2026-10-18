# DroidLab - Native Android Developer Toolkit

A comprehensive native Android app for developers, featuring:
- Dashboard with quick access to tools
- Device information (Android version, model, RAM, storage)
- JSON formatter/validator
- Base64 encoder/decoder
- SHA-256 hashing tool
- Unit converter (length, weight, temperature, volume)
- Notes manager with local storage
- Task manager with local storage
- Terminal-style interface with safe built-in commands
- Settings
- Offline support
- Dark/Light themes

## Features

### Dashboard
Welcome screen with device info preview and quick action buttons to navigate to different tools.

### Device Info
Displays detailed information about the device:
- Manufacturer and model
- Android version and API level
- CPU ABI
- Total and available RAM
- Storage usage

### Tools
- **JSON Formatter & Validator**: Validate and pretty-print JSON
- **Base64 Encoder/Decoder**: Encode text to Base64 or decode Base64 to text
- **SHA-256 Hasher**: Generate SHA-256 hash of input text
- **Unit Converter**: Convert between various units (length, weight, temperature, volume)

### Notes Manager
Create, view, and manage notes with local storage persistence.

### Task Manager
Create tasks with title, description, completion status, and timestamps.

### Terminal
Simple terminal interface for executing safe shell commands (limited to basic commands for safety).

### Settings
Placeholder for future settings (theme toggle, etc.)

## Screenshots

*(Add screenshots here)*

## Installation

1. Download the latest APK from the [GitHub Releases](#) page
2. Install on your Android device (requires Android 7.0+ / API 24+)
3. Grant necessary permissions if prompted

## Build from Source

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK with API 35

### Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Connect an Android device or start an emulator
5. Click Run

### Generating Signed APK
1. Build > Generate Signed Bundle / APK
2. Follow the wizard

## Architecture

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM (simplified for this app)
- **Data Storage**: Android DataStore (Preferences) for notes and tasks
- **Dependencies**: 
  - AndroidX Core, Lifecycle, Activity Compose
  - Jetpack Compose BOM (Material3)
  - Navigation Compose
  - WorkManager
  - DataStore Preferences
  - OKHttp
  - Commons Compress
  - Timber
  - Coil
  - Gson (for JSON parsing)
  - Termux Terminal View and Emulator (for future terminal implementation)

## Permissions

The app requests the following permissions:
- INTERNET (for future online features)
- POST_NOTIFICATIONS (for service notifications)
- FOREGROUND_SERVICE (to keep background services alive)
- FOREGROUND_SERVICE_SPECIAL_USE (Android 13+)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Based on the UbuntuDeck project
- Uses [Termux Terminal](https://github.com/termux/termux-app) for terminal components
- Uses [Proot](https://github.com/Proot-me/Proot) for Linux environment (in original UbuntuDeck)
