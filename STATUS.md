# UbuntuDeck - Status

## Phase 2 (Scaffold) - done
- Project structure created, stubs marked [STUB]
- Gradle config with extractNativeLibs=true, useLegacyPackaging=true
- LICENSE (GPLv3), NOTICE, CI workflow added
- All screens have stub UI

## Phase 3 (Rootfs + proot + terminal) - STUB / NOT IMPLEMENTED
- RootfsManager: stub - isRootfsInstalled() returns false, getRequiredFreeSpaceBytes() returns 7GB
- DownloadManager: stub - throws NotImplementedError
- ProotLauncher: stub - isProotAvailable() returns false
- TerminalView: stub UI - shows "[STUB] Terminal view - not implemented"
- Rootfs URL/size/checksum: UNPINNED (in ASSETS.md)
- proot binary: NOT included in jniLibs/arm64-v8a/ (directory is empty)
- proot loader: not documented with source/checksum
- Termux terminal libraries: INCLUDED in build.gradle.kts as `com.termux:terminal-view:0.118.1` and `com.termux:terminal-emulator:0.118.1` (Maven coordinates, not JitPack)

## Phase 4 (Desktop/VNC) - STUB / NOT IMPLEMENTED
- DesktopScreen: stub UI
- VncClientView: stub UI

## Phase 5 (Installer/service) - STUB / NOT IMPLEMENTED
- UbuntuDeckService: foreground service skeleton
- InstallerScreen: stub UI

## Phase 6 (Polish/release) - STUB / NOT IMPLEMENTED
- SettingsScreen: stub UI
- No signing keystore configured

**Device testing:** NOT possible. All device-facing gates UNTESTED ON DEVICE.
[took 0.2s]
