#!/bin/bash
# Verum Omnis Forensic Engine - Build Script
# Offline Android build script

set -e

echo "🔨 Building Verum Omnis Forensic Engine APK..."
echo ""

# Check for Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install JDK 17 or later."
    exit 1
fi

# Check for ANDROID_HOME or ANDROID_SDK_ROOT
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "⚠️  ANDROID_HOME not set. Checking if local.properties exists..."
    if [ ! -f "local.properties" ]; then
        echo "❌ local.properties not found and ANDROID_HOME not set."
        echo "   Please set ANDROID_HOME or create local.properties with:"
        echo "   sdk.dir=/path/to/your/android/sdk"
        exit 1
    fi
else
    # Create local.properties if it doesn't exist
    if [ ! -f "local.properties" ]; then
        SDK_PATH="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
        echo "sdk.dir=$SDK_PATH" > local.properties
        echo "📍 Created local.properties with SDK path: $SDK_PATH"
    fi
fi

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "📦 Building Debug APK..."
./gradlew assembleDebug

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo ""
    echo "✅ Build successful!"
    echo "📍 APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    
    # Calculate APK hash
    echo "🔐 APK SHA-512 Hash:"
    sha512sum app/build/outputs/apk/debug/app-debug.apk
else
    echo ""
    echo "❌ Build failed!"
    exit 1
fi
