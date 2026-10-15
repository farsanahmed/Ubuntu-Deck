# UbuntuDeck - Phase 1 Plan

## Overview
Build a native Android app "UbuntuDeck" that runs Ubuntu 22.04 (Jammy) XFCE4 desktop fully inside the app without Termux or Termux:X11, using Kotlin + Jetpack Compose targeting arm64-v8a.

## Reference Analysis
Based on https://github.com/AbuZar-Ansarii/real-ubuntu-for-android, the original approach:
- Uses udroid (proot-based Ubuntu environment) + XFCE4 + Termux:X11
- Runs in Termux environment with proot
- Requires external Termux apps for terminal/X11

We need to reproduce this functionality natively inside the app.

## Architecture

### 1. Rootfs Management
- **Source**: Ubuntu Jammy arm64 rootfs (from udroid/ubuntu-on-android images)
- **Storage**: Extract to app private storage (`context.filesDir` or `context.getExternalFilesDir(null)`)
- **Size**: ~7GB, need free space check before download
- **Download**: Resumable HTTP download with progress UI (use `DownloadManager`, `OkHttp` with range requests, or `WorkManager`)
- **Extraction**: Extract compressed rootfs (tar.gz) with progress tracking
- **Verification**: Optional checksum if available

### 2. proot Integration (Native)
- **Challenge**: Android 10+ blocks executing binaries from app data dir directly in some cases; we must ship proot and loader as native libs in `jniLibs/arm64-v8a/`
- **Binaries needed**: `proot`, `libtalloc.so` (or statically linked), and possibly `proot-static` variant
- **Source**: Prebuilt static proot for arm64 (e.g. from proot-static projects like ZhymabekRoman/proot-static)
- **Execution**: Launch via `ProcessBuilder` with `context.applicationInfo.nativeLibraryDir`
- **Binds**: `/dev`, `/proc`, `/sys`, and shared storage access (`/sdcard`, app storage)
- **Environment**: Set up proper PATH, HOME, DISPLAY, etc.

### 3. Display (VNC)
- **VNC Server inside Ubuntu**: TigerVNC (`tigervncserver`) running on localhost only with random password
- **VNC Client in app**: Embedded VNC viewer (e.g. `com.github.tigervnc:tigervnc-viewer` style, or use libraries like `androidVNC` fork, or `bVNC`). Alternatively use `Termux:X11` protocol? But requirement says "embedded VNC client view" - we need to render in Compose.
- **Features**: Touch-as-mouse, on-screen keyboard, pinch zoom, resolution settings
- **Port**: Random local port (e.g. 5901) bound to localhost only

### 4. Terminal
- **Library**: Termux `terminal-emulator` and `terminal-view` (available on Maven)
- **Integration**: Embed in a tab (Compose `HorizontalPager` or `TabRow`)
- **Connection**: Attach to proot shell process (e.g. `/bin/bash` inside rootfs)

### 5. Foreground Service
- **Purpose**: Keep proot/VNC session alive
- **Notification**: Persistent notification with controls (stop session)
- **Android 12+ warning**: Document phantom process killing, provide ADB instructions
- **Type**: `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` or appropriate type

### 6. Package Installer & Presets
- **One-tap installs in Ubuntu**: For Ollama, Claude Code, Firefox
- **Presets**: Cursor with `--no-sandbox --disable-gpu`, Antigravity with `OPENSSL_ia32cap="~0x4000000000000000" --no-sandbox --disable-gpu --disable-software-rasterizer`
- **Implementation**: Run shell commands inside proot session

## Dependencies

### Android/Compose
- `androidx.core:core-ktx`, `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.activity:activity-compose`, `androidx.compose.bom`
- `androidx.navigation:navigation-compose` (for tabs/screens)
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.work:work-runtime-ktx` (for resumable downloads)
- `androidx.datastore:datastore-preferences` (settings)

### Networking/Download
- `com.squareup.okhttp3:okhttp` (resumable downloads with range headers)
- `org.apache.commons:commons-compress` (tar extraction)

### Terminal (Termux)
- `com.termux:terminal-view:0.118.1` or latest
- `com.termux:terminal-emulator:0.118.1`

### VNC Client
- Need to evaluate options. Could use `com.gauravk.avnc:avnc` (modern Android VNC) or integrate tightvnc viewer. Alternatively use `androidVNC` - but license considerations.

### Other
- `com.jakewharton.timber:timber` (logging)
- `io.coil-kt:coil-compose` (icons if needed)

## Project Structure
```
UbuntuDeck/
├── app/
│   ├── src/main/
│   │   ├── cpp/ (if we need native glue, maybe not)
│   │   ├── jniLibs/arm64-v8a/
│   │   │   ├── proot
│   │   │   └── libtalloc.so (if needed)
│   │   ├── java/.../ubuntudeck/
│   │   │   ├── data/ (rootfs manager, download, extract)
│   │   │   ├── di/ (Hilt if used, or manual)
│   │   │   ├── service/ (Foreground service)
│   │   │   ├── terminal/ (Termux terminal integration)
│   │   │   ├── vnc/ (VNC client)
│   │   │   ├── ui/ (Compose screens)
│   │   │   │   ├── screens/
│   │   │   │   │   ├── SetupScreen (download/extract)
│   │   │   │   │   ├── TerminalScreen
│   │   │   │   │   ├── DesktopScreen (VNC)
│   │   │   │   │   ├── InstallerScreen
│   │   │   │   │   └── SettingsScreen
│   │   │   └── utils/ (proot launcher, storage checks)
│   │   └── res/
├── build.gradle.kts
└── settings.gradle.kts
```

## Permissions
- `INTERNET` (download)
- `POST_NOTIFICATIONS` (Android 13+)
- `FOREGROUND_SERVICE` 
- `FOREGROUND_SERVICE_SPECIAL_USE` (if applicable)
- `MANAGE_EXTERNAL_STORAGE`? Probably not needed - use app private storage and `ACTION_OPEN_DOCUMENT_TREE` for shared access if needed, or just mount common dirs
- `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` (legacy)

## Risks & Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Finding compatible proot binary for arm64 | High | Use prebuilt static proot from proot-static repos (ZhymabekRoman/proot-static) |
| Termux libraries licensing (GPLv3) | High | App must be GPLv3 licensed. Need to include license, source. |
| proot is GPL | High | App must be GPLv3 compatible |
| Rootfs download size (7GB) | Medium | Check storage, resumable downloads, clear UI |
| Android exec restrictions | High | Ship in jniLibs, launch from nativeLibraryDir. Also use `file.setExecutable(true)` if needed |
| VNC performance on mobile | Medium | Optimize resolution, use tight encoding, consider quality settings |
| Phantom process killing (Android 12+) | High | Foreground service with notification, warn user with ADB commands to disable |
| Rootfs extraction is slow | Medium | Background with progress, show ETA |
| Missing dependencies in rootfs | Medium | Use known working udroid jammy:xfce4 rootfs |

## Licenses
- **proot**: GPLv2/GPLv3 (check specific version) - https://github.com/termux/proot
- **Termux terminal-emulator/view**: GPLv3 - https://github.com/termux/termux-app
- **TigerVNC**: GPLv2 - https://github.com/TigerVNC/tigervnc
- **XFCE4**: GPL/LGPL
- **Ubuntu rootfs**: Various (mostly GPL, some proprietary firmware in default images - but for user-space desktop it's fine)

**Conclusion**: Since we bundle GPL-licensed components (proot, Termux libs), the app itself must be licensed under GPLv3. We need to include a LICENSE file and attribution.

## Next Steps (Phase 2)
After approval: Scaffold project with Gradle, set up permissions, add jniLibs structure, configure dependencies.
