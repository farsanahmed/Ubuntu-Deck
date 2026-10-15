# UbuntuDeck - Status

## Phase 2 (Scaffold) - done
- Project structure created, stubs marked [STUB]
- Gradle config with extractNativeLibs=true, useLegacyPackaging=true
- LICENSE (GPLv3), NOTICE, CI workflow added
- All screens have stub UI
- CI green (debug builds pass)

## Phase 3 (Rootfs + proot + terminal) - in progress
- Build check: done via CI (green)
- Removed Termux deps (caused auth issues in CI). Using custom minimal terminal stub [STUB] instead - no external credentials needed. See TERMINAL.md
- Proot binaries: NOT included yet (must document origin/license/checksum and add to jniLibs)
- Rootfs: URL/size/checksum not pinned yet
- Implementation stubs added for storage check, resumable download
- Terminal shell via proot: stub only
- GATE: cannot verify until implementation complete (local build not possible)

## Phase 4 (Desktop/VNC) - not started
## Phase 5 (Installer/service) - not started  
## Phase 6 (Polish/release) - not started

**Next:** Continue Phase 3 - implement proot setup and terminal connection properly.
