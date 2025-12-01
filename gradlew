#!/bin/bash
# Gradle wrapper script

# Determine the script's directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Set default values
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Check for JAVA_HOME
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Check if Java is available
if ! command -v $JAVACMD &> /dev/null; then
    echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    exit 1
fi

# Download and run Gradle
GRADLE_VERSION="8.4"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_WRAPPER_JAR="$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin/gradle-$GRADLE_VERSION/lib/gradle-launcher-$GRADLE_VERSION.jar"

if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    mkdir -p "$GRADLE_USER_HOME/wrapper/dists"
    curl -sL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin.zip"
    unzip -q "$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin.zip" -d "$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin"
fi

# Run Gradle
exec "$JAVACMD" $DEFAULT_JVM_OPTS -jar "$GRADLE_WRAPPER_JAR" "$@"
