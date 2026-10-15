# Terminal Implementation

## What was removed
- Removed `com.termux:terminal-view:0.118.1` and `com.termux:terminal-emulator:0.118.1` from dependencies
- Removed jitpack.io repository (was causing auth issues in CI)

## Current state
- Terminal tab shows [STUB] placeholder
- Will implement minimal custom terminal view connected to proot process
- No external credentials needed

## Licensing
- Termux libs are GPLv3; by not vendoring them, we avoid redistributing GPLv3 code. We implement our own minimal terminal UI.
- Proot is GPLv2/GPLv3 - we document and ship as native binary per requirements.

## Next
Implement simple terminal view that can attach to a shell process (proot) via ProcessBuilder.
