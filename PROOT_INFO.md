# proot Binary Information

## Required files in jniLibs/arm64-v8a/
- proot (main binary)
- loader (or ensure proot can find its loader)

## Environment variables to set
- PROOT_LOADER = path to loader in nativeLibraryDir
- PROOT_TMP_DIR = writable app dir (e.g., cacheDir/proot_tmp)

## Launch requirements
- Must run from nativeLibraryDir (Android 10+ exec restrictions) - we set extractNativeLibs=true and useLegacyPackaging=true
- android:extractNativeLibs="true" in AndroidManifest.xml ✓
- useLegacyPackaging = true in build.gradle.kts ✓

## Source/license/checksum [STUB - to be documented]
- Origin: Termux proot (termux/proot) or proot-static build
- License: GPLv2/GPLv3
- Version: to be determined
- SHA256: to be determined
- Built for: arm64-v8a

Note: Do not bundle unverified binaries. Record source, license, checksum.
