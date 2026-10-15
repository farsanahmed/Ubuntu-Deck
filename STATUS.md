# UbuntuDeck - Status

## Phase 2 (Scaffold) - done
- Project structure, Gradle, permissions, jniLibs dir, CI workflow

## Phase 3 (Rootfs + proot + terminal) - CODE COMPLETE, UNTESTED ON DEVICE
- **NOT verified on a real device.** No Android device available in this environment.
- RootfsManager: download+extract+checksum verify implemented (Apache Commons Compress tar/gzip)
  - URL: https://github.com/RandomCoderOrg/ubuntu-on-android/releases/download/v3/udroid-arm64-xfce4-V3MBB3.tar.gz
  - SHA256: d5290068b99740da5198a355b5152e0198fff126f0865f4c4634e509508a6da8
  - Size: ~2GB compressed, ~7GB free space required
  - [GAP] tar extraction does NOT explicitly preserve symlinks/hardlinks (see Q2 below)
- Proot binary: ysdragon/proot-static v5.4.0 (GPLv2, static, self-contained, SHA256 verified)
  - BUT: .gitignore excludes app/src/main/jniLibs/**/proot -> binary NOT tracked in git. Must force-add or fix .gitignore before release.
- ProotLauncher: builds proot command with binds and PROOT_TMP_DIR
- Terminal: [STUB] - Termux terminal-emulator/terminal-view deps added via JitPack, real PTY wiring NOT implemented. Tab shows [STUB] placeholder.

## Phase 4 (Desktop/VNC) - STUB / NOT IMPLEMENTED
- DesktopScreen: [STUB] placeholder. No VNC client, no TigerVNC/XFCE4 integration.

## Phase 5 (Installer/service) - STUB / NOT IMPLEMENTED
- UbuntuDeckService: foreground service skeleton (notification only). No session management.
- InstallerScreen: [STUB] placeholders. No package installs, no presets, no error handling/log viewer.

## Phase 6 (Polish/release) - STUB / NOT IMPLEMENTED
- SettingsScreen: [STUB] layout only. No actual settings logic.
- LICENSE (GPLv3) and NOTICE files present.

**Device testing:** NOT possible. All device-facing gates UNTESTED ON DEVICE.
[took 0.3s]
