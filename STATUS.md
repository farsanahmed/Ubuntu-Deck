# UbuntuDeck - Status

## Phase 2 (Scaffold) - done
- Project structure, Gradle, permissions, jniLibs dir, CI workflow

## Phase 3 / Phase A (Rootfs + proot + terminal) - CODE COMPLETE, CI GREEN, UNTESTED ON DEVICE
- CI green: https://github.com/farsanahmed/Ubuntu-Deck/actions/runs/38816620005 (commit 623d2a7)
- RootfsManager.kt (REAL implementation, not stub):
  - Resumable download via OkHttp Range requests
  - SHA-256 checksum verification after download
  - tar/gzip extraction via Apache Commons Compress
  - Preserves symlinks, hardlinks, permissions, exec bits
- Rootfs pinned: https://github.com/RandomCoderOrg/ubuntu-on-android/releases/download/v3/udroid-arm64-xfce4-V3MBB3.tar.gz (~2GB compressed, SHA256 d529...a6da8, ~7GB free required)
- Proot binary: jniLibs/arm64-v8a/proot (1,560,648 bytes), from ysdragon/proot-static v5.4.0, GPLv2, SHA256 45e8...ce79, static-linked self-contained
- ProotLauncher.kt (REAL implementation):
  - Binds: /dev:/dev, /proc:/proc, /sys:/sys, app files dir, external storage dirs, /storage, /sdcard
  - Env: PROOT_TMP_DIR, HOME, PATH, TERM, LANG
  - Captures combined stdout+stderr via ProcessBuilder.redirectErrorStream(true)
- TerminalScreen.kt (REAL UI):
  - Free-space check vs 7GB required
  - Download size display (~2GB compressed)
  - Download / Extract buttons with progress
  - "Run test command" button: runs `cat /etc/os-release && uname -a` through proot, shows real stdout+stderr
  - Log area with raw output
- DownloadManager.kt stub REMOVED (dead code - RootfsManager handles download)
- PROOT_INFO.md updated with real source/license/checksum

## Phase 4 (Desktop/VNC) - STUB / NOT IMPLEMENTED
- DesktopScreen: stub UI only
- VncClientView: stub UI only

## Phase 5 (Installer/service) - STUB / NOT IMPLEMENTED
- UbuntuDeckService: foreground service skeleton
- InstallerScreen: stub UI only

## Phase 6 (Polish/release) - STUB / NOT IMPLEMENTED
- SettingsScreen: stub UI only

## Build/Release Setup
- gradlew: minimal script that delegates to system gradle (CI provides it via setup-gradle)
- CI: JDK 17 + assembleDebug, debug APK uploaded as artifact
- Release job on tags v* (not yet used - waiting for signing keystore secrets)
- NO release published yet (waiting for your confirmation of keystore secrets)

**Device testing:** NOT possible. All device-facing gates UNTESTED ON DEVICE.
- Rootfs download/extraction: not tested on device
- proot execution: not tested on device
- Terminal PTY to proot shell: not implemented (TerminalView.kt still stub)
- XFCE4 desktop: not implemented
- Package installs: not implemented
[took 0.3s]
