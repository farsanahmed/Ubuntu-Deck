package com.ubuntudeck.ubuntudeck.data

object ProotConfig {
    // Rootfs configuration - to be pinned in implementation
    const val ROOTFS_ARCHIVE_NAME = "jammy-xfce4-arm64.tar.gz"
    // NOTE: Source, URL, real download size, and SHA256 must be verified and documented
    const val ROOTFS_REQUIRED_FREE_GB = 7L
    
    // proot configuration
    const val PROOT_BIN_NAME = "proot"
    const val PROOT_LOADER_NAME = "proot-loader" // or as needed
    
    // Environment
    const val PROOT_TMP_DIR_NAME = "proot_tmp"
}
[took 0.2s]
