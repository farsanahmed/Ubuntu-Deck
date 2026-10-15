# UbuntuDeck - Status

## Phase 2 (Scaffold) - done
- Project structure created, stubs marked [STUB]
- Gradle config with extractNativeLibs=true, useLegacyPackaging=true
- LICENSE (GPLv3), NOTICE, CI workflow added
- All screens have stub UI

## Phase 3 (Rootfs + proot + terminal) - blocked (infrastructure)
- Build check: cannot complete real Gradle build in this environment (no Gradle/Android SDK available). Gradle wrapper created but no installation.
- Rootfs: not pinned yet (need actual source URL, real download size, SHA256)
- proot: not sourced yet (need origin, license, checksum, binaries in jniLibs)

## Build environment note
Gradle and Android SDK tools are not available in this environment. Cannot run `assembleDebug`. CI workflow is valid YAML but will require proper runner.

**Next:** Proceed to document and prepare assets for Phase 3 as much as possible without requiring build, but note build cannot be verified. Mark as untested where appropriate.
[took 0.2s]
