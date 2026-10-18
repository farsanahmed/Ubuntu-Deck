# Build Instructions for DroidLab

The app is set up to build automatically via GitHub Actions.

## Automatic Build

1. Go to the GitHub repository: https://github.com/farsanahmed/Ubuntu-Deck
2. Click on the "Actions" tab.
3. You should see a workflow named "Build DroidLab Debug APK".
4. The workflow runs on every push to main, master, develop branches and on pull requests.
5. You can also manually trigger it via the "Run workflow" button.

## Manual Build (if you have the environment set up)

If you have a working Java and Android SDK environment, you can build locally:

```bash
# Clone the repository
git clone https://github.com/farsanahmed/Ubuntu-Deck.git
cd Ubuntu-Deck

# Make sure gradlew is executable
chmod +x gradlew

# Build the debug APK
./gradlew assembleDebug

# The APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

## Installing the APK

### From GitHub Actions Artifact

1. After the workflow run completes, click on the run.
2. Look for the "Artifacts" section.
3. Download the artifact named "droidlab-debug-apk".
4. Extract the ZIP file to get the APK.

### To install on Android device

1. Transfer the APK to your device (via USB, email, cloud storage, etc.).
2. On your device, open the APK file to install.
3. You may need to enable "Install from unknown sources" in Settings.

### To save to Downloads folder (as requested)

If you have ADB access:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
# Or to push to Downloads:
adb push app/build/outputs/apk/debug/app-debug.apk /storage/emulated/0/Download/DroidLab.apk
```

## APK Verification

The workflow includes a step to verify that the native library `libproot.so` is packaged in the APK for arm64-v8a architecture.

## Notes

- The app requires Android 7.0 (API level 24) or higher.
- It is optimized for arm64-v8a devices.
- The app works offline; no internet connection is required for core features.
