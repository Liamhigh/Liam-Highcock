# Build Instructions for Verum Omnis Forensic Engine

This document provides detailed instructions for building the Verum Omnis Forensic Engine Android application.

## Prerequisites

### Required Software

1. **Android Studio**
   - Version: Hedgehog (2023.1.1) or later
   - Download: https://developer.android.com/studio

2. **Java Development Kit (JDK)**
   - Version: Java 17 (OpenJDK recommended)
   - The project is configured to use Java 17

3. **Android SDK**
   - Minimum SDK: API 24 (Android 7.0)
   - Target SDK: API 34 (Android 14)
   - Build Tools: Version 34.0.0 or later

### SDK Components Required

Through Android Studio SDK Manager, ensure you have:
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android SDK Platform-Tools
- Android Emulator (optional, for testing)
- System images for testing (optional)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/Liamhigh/Liam-Highcock.git
cd Liam-Highcock
git checkout copilot/create-complete-build-branch
```

### 2. Configure Android SDK Path

#### Option A: Let Android Studio Configure Automatically (Recommended)
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository directory
4. Android Studio will automatically create `local.properties` with your SDK path

#### Option B: Manual Configuration
1. Copy the template:
   ```bash
   cp local.properties.template local.properties
   ```

2. Edit `local.properties` and set your Android SDK path:
   - **Windows**: `sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\sdk`
   - **macOS**: `sdk.dir=/Users/yourusername/Library/Android/sdk`
   - **Linux**: `sdk.dir=/home/yourusername/Android/Sdk`

### 3. Open Project in Android Studio

1. Launch Android Studio
2. Click "Open" or "Open an Existing Project"
3. Navigate to the cloned repository directory
4. Click "OK"
5. Wait for Gradle sync to complete (this may take several minutes on first run)

### 4. Resolve Dependencies

Android Studio will automatically download all required dependencies during the Gradle sync process. This includes:
- AndroidX libraries
- CameraX components
- iText7 PDF library
- Kotlin coroutines
- Room database
- Other dependencies listed in `app/build.gradle.kts`

**Note**: First-time sync requires internet access to download dependencies from Maven Central and Google's Maven repository.

## Building the Application

### Using Android Studio

#### Build Debug APK
1. In Android Studio menu: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
2. Wait for build to complete
3. APK location will be shown in notification (typically: `app/build/outputs/apk/debug/app-debug.apk`)

#### Build Release APK
1. In Android Studio menu: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
2. Select "release" variant if prompted
3. APK will be at: `app/build/outputs/apk/release/app-release.apk`

**Note**: Release APK will use debug signing unless you configure release signing (see below).

### Using Command Line (Gradle)

#### Build Debug APK
```bash
# macOS/Linux
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

#### Build Release APK
```bash
# macOS/Linux
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

#### Clean Build
```bash
# macOS/Linux
./gradlew clean

# Windows
gradlew.bat clean
```

### Running Tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# View test results
open app/build/reports/tests/testDebugUnitTest/index.html
```

## Release Signing Configuration (Optional)

For production releases, you should sign the APK with your own keystore.

### 1. Create a Keystore (if you don't have one)

```bash
keytool -genkey -v -keystore release.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configure Signing

The build is configured to use environment variables for signing. Set these before building:

```bash
export KEYSTORE_PATH=/path/to/your/release.keystore
export KEYSTORE_PASSWORD=your_keystore_password
export KEY_ALIAS=release
export KEY_PASSWORD=your_key_password
```

Then build the release APK:
```bash
./gradlew assembleRelease
```

**Important**: Never commit your keystore or passwords to version control!

## Troubleshooting

### Common Issues

#### Gradle Sync Failed
- **Solution**: Check your internet connection and retry sync
- Verify Android SDK path in `local.properties`
- Clear Gradle cache: `./gradlew clean --refresh-dependencies`

#### SDK Not Found
- **Solution**: 
  - Open SDK Manager in Android Studio: `Tools > SDK Manager`
  - Install Android SDK Platform 34
  - Install Build Tools 34.0.0

#### Java Version Mismatch
- **Solution**: 
  - Ensure JDK 17 is installed
  - In Android Studio: `File > Project Structure > SDK Location`
  - Set JDK location to Java 17

#### Build Fails with "Could not resolve..."
- **Solution**: 
  - Check internet connection
  - Try with VPN if corporate network blocks Maven repositories
  - Clear Gradle cache and rebuild

#### Out of Memory During Build
- **Solution**: Edit `gradle.properties`:
  ```properties
  org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
  ```

## Project Structure

```
Liam-Highcock/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/org/verumomnis/forensic/
│   │   │   │   ├── core/         # Core forensic engine
│   │   │   │   ├── crypto/       # Cryptographic sealing
│   │   │   │   ├── database/     # Room database
│   │   │   │   ├── leveler/      # B1-B9 analysis engine
│   │   │   │   ├── location/     # GPS services
│   │   │   │   ├── pdf/          # PDF generation
│   │   │   │   ├── report/       # Narrative generation
│   │   │   │   ├── tax/          # Tax return engine
│   │   │   │   └── ui/           # Activities and UI
│   │   │   └── res/              # Resources
│   │   └── test/                 # Unit tests
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
├── gradlew                       # Gradle wrapper (Unix)
├── gradlew.bat                   # Gradle wrapper (Windows)
└── local.properties.template     # Template for local config
```

## Build Configuration

### Gradle Version
- Gradle: 8.4
- Android Gradle Plugin (AGP): 8.1.4
- Kotlin: 1.9.21

### Compile Options
- Source Compatibility: Java 17
- Target Compatibility: Java 17
- JVM Target: 17

### Build Variants
- **debug**: Debug build with no minification, debug signing
- **release**: Release build with ProGuard/R8 minification

## Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Gradle Build Tool](https://docs.gradle.org/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Project README](README.md)

## Getting Help

If you encounter issues:
1. Check this BUILD.md for solutions
2. Review the GitHub Issues page
3. Ensure all prerequisites are met
4. Try a clean build: `./gradlew clean assembleDebug`

## License

See [README.md](README.md) for license information.
