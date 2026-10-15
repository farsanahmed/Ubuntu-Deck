# External Assets - Documentation

This document tracks all external binaries and archives with their sources, licenses, and checksums as required.

## Rootfs

**Name:** Ubuntu Jammy (22.04) XFCE4 for arm64 (udroid)
**Required free space:** ~7GB (as specified)
**Note:** Actual download size and SHA256 must be determined and pinned before download. The reference uses udroid from RandomCoderOrg/ubuntu-on-android. Current known locations:
- https://github.com/RandomCoderOrg/udroid-download (releases may have tarballs)
- Files typically named like: `jammy-xfce4-arm64.tar.gz` or similar

**Status:** UNPINNED - needs verification of actual URL, size, checksum

## proot binary

**Purpose:** PRoot is a user-space implementation of chroot, mount --bind, binfmt_misc
**License:** GPLv2/GPLv3 (per proot project)
**Source:** https://github.com/termux/proot (Termux's fork/version)
**Architecture:** arm64-v8a
**Status:** NOT INCLUDED - must obtain with checksum before bundling

**Loader:** proot needs its loader binary; also needs to be shipped. Must be documented.

## Termux Libraries
**terminal-emulator, terminal-view** - included via Maven dependencies (GPLv3) as specified.

**TigerVNC** - runs inside rootfs, not bundled in APK.

## Notes
All binaries placed in jniLibs/arm64-v8a/ must be set executable. Will run from nativeLibraryDir with PROOT_LOADER and PROOT_TMP_DIR set.
