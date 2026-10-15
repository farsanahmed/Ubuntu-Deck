#!/bin/sh

# Simplified gradlew for environment - actual gradle wrapper should be regenerated if needed
# For now, create symlink or use system gradle if available
echo "Note: Using gradle from PATH if available"
if command -v gradle >/dev/null 2>&1; then
    gradle "$@"
else
    echo "Gradle not found in PATH"
    exit 1
fi
[took 0.0s]
