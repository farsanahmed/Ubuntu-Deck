# proot Binary Information

## Required files in jniLibs/arm64-v8a/
- proot (main binary, statically linked, self-contained)

## Source / License / Checksum
- **URL:** https://github.com/ysdragon/proot-static/releases/download/v5.4.0/proot-aarch64-static
- **Release:** ysdragon/proot-static v5.4.0 (2024-09-01)
- **Architecture:** aarch64 (arm64), ELF64 static binary
- **SHA256:** `45e81182f839a1752d8c8408e7d4569a58ae51d4331326d7f662a105f859ce79`
- **Size:** 1,560,648 bytes
- **License:** GPLv2 (proot-me/proot upstream, copyright STMicroelectronics)
- **Static-linked:** Confirmed — no .interp section, no dynamic section, zero NEEDED shared-library entries
- **Separate loader:** NOT needed (self-contained static binary)

## Environment variables to set
- `PROOT_LOADER` = path to loader in nativeLibraryDir (NOT needed for static build)
- `PROOT_TMP_DIR` = writable app dir (e.g., cacheDir/proot_tmp)

## Launch requirements
- Must run from nativeLibraryDir (Android 10+ exec restrictions) - we set extractNativeLibs=true and useLegacyPackaging=true
- android:extractNativeLibs="true" in AndroidManifest.xml ✓
- useLegacyPackaging = true in build.gradle.kts ✓

## Verification
- Binary tracked in git: app/src/main/jniLibs/arm64-v8a/proot
- SHA256 verified against downloaded file
- Checksum recorded in ASSETS.md

## Note
Do not bundle unverified binaries. Record source, license, checksum for every external artifact.
