#!/bin/sh
# Gradle wrapper - delegates to system gradle if available, otherwise uses wrapper jar
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

APP_HOME=$(cd "$(dirname "$0")" && pwd)
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD=$(command -v java 2>/dev/null || echo java)
fi

exec "$JAVACMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
