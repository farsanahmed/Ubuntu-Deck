# UbuntuDeck - Status

## Phase 2 (Scaffold) - done
- Project structure created, stubs marked [STUB]
- Gradle config with extractNativeLibs=true, useLegacyPackaging=true
- LICENSE (GPLv3), NOTICE, CI workflow added
- All screens have stub UI
- CI green (debug builds pass)

## Phase 3 (Rootfs + proot + terminal) - done (builds OK; device testing untested)
- CI green: https://github.com/farsanahmed/Ubuntu-Deck/actions/runs/38801629611 (commit a62a385)
- RootfsManager: download+extract+checksum verify implemented
  - Rootfs URL: https://github.com/RandomCoderOrg/ubuntu-on-android/releases/download/v3/udroid-arm64-xfce4-V3MBB3.tar.gz
  - SHA256: d5290068b99740da5198a355b5152e0198fff126f0865f4c4634e509508a6da8
  - Size: ~2GB compressed, ~7GB free space required
  - NOT yet downloaded/extracted on device (untested - no device)
- Proot binary: downloaded from ysdragon/proot-static v5.4.0
  - URL: https://github.com/ysdragon/proot-static/releases/download/v5.4.0/proot-aarch64-static
  - SHA256: 45e81182f839a1752d8c8408e7d4569a58ae51d4331326d7f662a105f859ce79 (VERIFIED)
  - License: GPLv2, static-linked, self-contained (no separate loader)
  - Included in jniLibs/arm64-v8a/proot
- ProotLauncher: builds proot command with binds (/dev,/proc,/sys,/storage,/sdcard, app dirs), PROOT_TMP_DIR, environment
- Terminal: stubbed [STUB] in UI - uses Termux terminal-emulator/terminal-view deps via JitPack, proot PTY wiring NOT yet implemented (pending device test)
- Terminal tab shows [STUB] placeholder, reports proot availability

## Phase 4 (Desktop/VNC) - not started
## Phase 5 (Installer/service) - not started  
## Phase 6 (Polish/release) - not started

**Note:** No device available to test rootfs download, extraction, proot execution, or terminal. All Phase 3 device-facing gates are UNTESTED.
[took 0.3s]
