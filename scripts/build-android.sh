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
