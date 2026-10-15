# Phase 2 - Build Info

## Scaffold Completed
- Project structure created with all required directories
- Gradle configuration with correct settings (arm64-v8a only, extractNativeLibs=true, useLegacyPackaging=true)
- All screens stubbed with [STUB] markers as required
- Service, data managers, utils stubbed
- LICENSE (GPLv3) and NOTICE files added
- GitHub Actions CI workflow added for debug APK builds
- .gitignore configured

## What's stubbed (clearly marked)
- TerminalScreen - shows [STUB] placeholder
- DesktopScreen - shows [STUB] placeholder  
- InstallerScreen - shows [STUB] placeholder
- SettingsScreen - shows [STUB] placeholder
- RootfsManager - all methods stubbed
- ProotLauncher - all methods stubbed
- TerminalView - stub
- VncClientView - stub
- UbuntuDeckService - basic structure only (no actual proot session management)

## Build readiness
The app scaffolds correctly. However, the build environment in this harness may not have Android SDK/Gradle wrapper fully set up. The project structure is correct and follows Android best practices.

## Next Phase (Phase 3)
1. Identify rootfs source (jammy xfce4 arm64) - need to determine actual download URL, size, and SHA256 checksum
2. Source proot binary and loader for arm64 with licenses/checksums
3. Add to jniLibs
4. Implement rootfs download with resume + storage check
5. Implement extraction
6. Get proot shell working in terminal

**Note on proot source:** We need to either build from Termux sources or obtain from Termux package with proper attribution and checksum. This must be documented before including binaries.
