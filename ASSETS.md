# External Assets - Documentation

This document tracks all external binaries and archives with their sources, licenses, and checksums as required.

## Rootfs

**Name:** Ubuntu Jammy (22.04) XFCE4 for arm64 (udroid)
**File name:** `udroid-arm64-xfce4-V3MBB3.tar.gz`
**Direct download URL:** `https://github.com/RandomCoderOrg/ubuntu-on-android/releases/download/v3/udroid-arm64-xfce4-V3MBB3.tar.gz`
**SHA256 checksum:** `d5290068b99740da5198a355b5152e0198fff126f0865f4c4634e509508a6da8`
**Size:** 2,012,215,629 bytes (~2.01 GB compressed)
**Required free space after extraction:** ~7GB (as specified)
**Source of truth:** `https://raw.githubusercontent.com/RandomCoderOrg/udroid-download/main/distro-data.json` (jammy → xfce4 → aarch64)
**Release:** RandomCoderOrg/ubuntu-on-android tag v3 ("UDROID v3 file system")
**Status:** PINNED AND VERIFIED via GitHub API (asset ID 171117435, state uploaded)

## proot binary (native, bundled in APK)

**File:** `app/src/main/jniLibs/arm64-v8a/proot`
**URL:** `https://github.com/ysdragon/proot-static/releases/download/v5.4.0/proot-aarch64-static`
**Release:** ysdragon/proot-static v5.4.0 (2024-09-01)
**Architecture:** aarch64 (arm64), ELF64
**Static-linked:** Confirmed — no .interp, no dynamic section, zero NEEDED entries (self-contained, no separate loader needed)
**SHA256:** `45e81182f839a1752d8c8408e7d4569a58ae51d4331326d7f662a105f859ce79` — VERIFIED against downloaded file
**Size:** 1,560,648 bytes
**License:** GPLv2 (proot-me/proot upstream, copyright STMicroelectronics)
**Status:** DOWNLOADED, CHECKSUM VERIFIED, INCLUDED IN jniLibs

## proot loader

**Note:** proot static binary is self-contained — NO separate loader needed. If we later switch to a split proot+loader model, use ZhymabekRoman/proot-static (armhf binaries that run on arm64, need loader).

## Termux Libraries

**terminal-emulator, terminal-view** — version 0.118.1 from JitPack (`com.github.termux.termux-app`)
**License:** GPLv3
**Source:** https://github.com/termux/termux-app
**Status:** Added via Gradle dependency (not vendored)

## TigerVNC

**Used inside Ubuntu rootfs** (installed via apt inside Ubuntu, not bundled in APK)
**License:** GPLv2
**Source:** https://github.com/TigerVNC/tigervnc

## XFCE4 Desktop Environment

**Used inside Ubuntu rootfs** (installed via apt inside Ubuntu)
**License:** GPL/LGPL
**Source:** https://xfce.org/

## Notes

- All binaries in jniLibs/arm64-v8a/ are extracted to nativeLibraryDir at install time via `useLegacyPackaging = true`.
- proot will be launched via ProcessBuilder from nativeLibraryDir. PROOT_TMP_DIR set to a writable app directory.
- No Termux app dependency — terminal view comes from Gradle dependency.
