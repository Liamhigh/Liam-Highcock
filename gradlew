#!/bin/bash
# Gradle wrapper script for Unix

# Determine the script's location
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
APP_HOME="$SCRIPT_DIR"

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

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
