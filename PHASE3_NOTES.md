# Phase 3 - Notes (Initial)

## Rootfs information (to be determined/pinned)
We need to identify a stable source for Ubuntu Jammy (22.04) XFCE4 arm64 rootfs. Common sources from udroid:
- RandomCoderOrg/udroid-download releases (historically provided tarballs)
- udroid images used by ubuntu-on-android

However, accessing specific release assets in this environment is limited. For the purpose of this implementation, we must:
1. Document the intended source, size, SHA256 when found
2. In production, the URL would be pinned. Since we cannot reliably fetch arbitrary external assets without more info, we document this as a requirement and provide stub infrastructure.

## proot binary
proot is GPLv2/GPLv3 licensed. The binary must come from a verifiable source (Termux package build from source, or known static build with checksum). 
Options:
- Build from Termux sources (proot in termux-packages) - requires build deps
- Use prebuilt from trusted source with documented checksum

We need to obtain proot and its loader. proot typically needs the loader binary (proot extracts it). We'll need to set:
- PROOT_LOADER to point to loader binary in nativeLibraryDir
- PROOT_TMP_DIR to writable app directory (e.g., context.cacheDir or filesDir/tmp)

## Approach for this phase
Implement the infrastructure first (classes, UI wiring), mark as [STUB] where binaries/rootfs not available, and document everything. The actual binaries/rootfs would be added when the source is verified.
[took 0.1s]
