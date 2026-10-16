# UbuntuDeck

A native Android app called "UbuntuDeck": a real Ubuntu 22.04 (Jammy) XFCE4 desktop running fully inside the app, with no Termux or Termux:X11 needed. Built with Kotlin + Jetpack Compose, targeting arm64-v8a.

## Phase 2 (Scaffold) - Status

**Build Status:** Scaffold created - ready to build

**What's implemented (Phase A):**
- RootfsManager: real implementation for download, verification, extraction
- ProotLauncher: real implementation for running proot with proper binds and environment
- TerminalScreen: real UI for rootfs management and test command execution
- libproot.so binary included in jniLibs/arm64-v8a

**What's stubbed:**
- TerminalView: [STUB] placeholder - awaiting PTY-based terminal implementation
- DesktopScreen: stub UI only
- VncClientView: stub UI only
- InstallerScreen: stub UI only
- UbuntuDeckService: foreground service skeleton
- SettingsScreen: skeleton with seccomp toggle (not wired to proot)

**What's next (Phase B):**
- Implement real interactive terminal in TerminalView using PTY connected to proot
- Wire SettingsScreen seccomp toggle to ProotLauncher environment
- Implement TigerVNC + XFCE4 desktop and embedded VNC viewer
- Implement installer screen, service presets, and polish

**Licensing:**
- This app is licensed under GPLv3 (see LICENSE)
- Bundles GPL components (proot, Termux libs) - see NOTICE
- Rootfs from udroid (Ubuntu-based) has various licenses

## Requirements
- arm64-v8a device
- ~7GB free storage (required)
- Android 10+ recommended
