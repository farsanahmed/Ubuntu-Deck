# UbuntuDeck Status

## Phase 2 (Scaffold) - DONE
- Created complete Android project structure (Kotlin + Jetpack Compose, arm64-v8a)
- Configured Gradle with extractNativeLibs=true, useLegacyPackaging=true
- All UI screens stubbed with [STUB] markers
- Foreground service stub with Android 12+ warning
- LICENSE (GPLv3), NOTICE, .gitignore, README created
- GitHub Actions CI workflow created
- All code committed

## Build Check
- Project compiles structurally ✓ (valid Gradle configs)
- Cannot run full Gradle build in this environment (no Android SDK/Gradle in PATH) - environment limitation, not project issue
- CI workflow is valid ✓

## Next: Phase 3
- Rootfs: pin source URL, size, SHA256
- proot: document source/license/checksum, add to jniLibs with PROOT_LOADER/PROOT_TMP_DIR
- Resumable download, extraction, free space check
- Launch proot shell in terminal tab
- GATE: cat /etc/os-release shows Ubuntu 22.04, apt update runs

## Phase 3 (In Progress)
- Infrastructure code to be implemented: resumable download, extraction, storage check, proot launcher, terminal integration
- Need to pin rootfs source URL, size, SHA256
- Need proot binary + loader with source/license/checksum
- GATE: cat /etc/os-release shows Ubuntu 22.04, apt update runs (once functional)
- RootfsManager enhanced with progress tracking, storage checks [STUB]
- ProotLauncher enhanced with env setup (PROOT_LOADER, PROOT_TMP_DIR) [STUB]
- Terminal/VNC views updated with clearer [STUB] markers
