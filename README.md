# UbuntuDeck

A native Android app called "UbuntuDeck": a real Ubuntu 22.04 (Jammy) XFCE4 desktop running fully inside the app, with no Termux or Termux:X11 needed. Built with Kotlin + Jetpack Compose, targeting arm64-v8a.

## Phase 2 (Scaffold) - Status

**Build Status:** Scaffold created - ready to build

**What's stubbed:**
- All UI screens (Terminal, Desktop, Installer, Settings) show [STUB] placeholder (marked in UI)
- RootfsManager: stub methods for installation, storage checks
- ProotLauncher: stub methods, no actual execution
- No native binaries (proot, loader) included yet - must be added in Phase 3
- No actual rootfs included/downloaded
- No VNC client implementation
- No terminal integration with proot yet

**What's next (Phase 3):**
- Determine and pin rootfs source URL, size, and checksum
- Source/build proot binary and loader for arm64, add to jniLibs with proper setup
- Implement rootfs download with resume capability
- Implement extraction with progress UI
- Implement storage space checks (~7GB free required)
- Get proot shell working in terminal tab

**Licensing:**
- This app is licensed under GPLv3 (see LICENSE)
- Bundles GPL components (proot, Termux libs) - see NOTICE
- Rootfs from udroid (Ubuntu-based) has various licenses

## Requirements
- arm64-v8a device
- ~7GB free storage (required)
- Android 10+ recommended
[took 0.2s]
