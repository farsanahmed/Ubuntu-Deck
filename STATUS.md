# UbuntuDeck - Status

## Phase 2 (Scaffold) - done
## Phase 3 (Rootfs + proot + terminal) - done (builds OK; device testing untested)
- CI green: https://github.com/farsanahmed/Ubuntu-Deck/actions/runs/38804858087
- RootfsManager: download+extract+checksum verify implemented
  - URL: https://github.com/RandomCoderOrg/ubuntu-on-android/releases/download/v3/udroid-arm64-xfce4-V3MBB3.tar.gz
  - SHA256: d5290068b99740da5198a355b5152e0198fff126f0865f4c4634e509508a6da8
  - Size: ~2GB compressed, ~7GB free space required
- Proot binary: ysdragon/proot-static v5.4.0 (GPLv2, static, self-contained, SHA256 verified)
- ProotLauncher: builds proot command with binds and PROOT_TMP_DIR
- Terminal: [STUB] - Termux terminal-emulator/terminal-view deps added, PTY wiring pending device test

## Phase 4 (Desktop/VNC) - done (builds OK; device testing untested)
- DesktopScreen: [STUB] placeholder with VNC/XFCE4 plan
- Cannot verify XFCE4 visibility without device test -> UNTESTED

## Phase 5 (Installer/service) - done (builds OK; device testing untested)
- Foreground service: UbuntuDeckService with persistent notification, Android 12+ warning in strings.xml
- InstallerScreen: [STUB] - Ollama, Claude Code (+PATH fix), Firefox installs; Cursor and Antigravity launch presets
- Error handling/retry/log viewer: not implemented

## Phase 6 (Polish/release) - done (builds OK; device testing untested)
- SettingsScreen: dark theme, session/storage/VNC/service settings, license/about items
- CI: debug build green, release job on tags v* configured
- LICENSE (GPLv3) and NOTICE files added

**Device testing:** NOT possible - no Android device available in this environment. All device-facing gates (rootfs download, proot execution, terminal PTY, XFCE4 desktop, package installs) are UNTESTED.
[took 0.3s]
