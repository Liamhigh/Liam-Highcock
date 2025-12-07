# Complete Build Branch for Android Studio

This branch (`copilot/create-complete-build-branch`) contains a ready-to-build configuration of the Verum Omnis Forensic Engine that you can clone and immediately open in Android Studio.

## What's Included

This branch has been specifically prepared with:

✅ **Complete Android Project Structure**
- All source code (Kotlin)
- All resources (layouts, drawables, strings)
- AndroidManifest.xml with all activities
- ProGuard rules for release builds

✅ **Build Configuration**
- Gradle 8.4 wrapper
- Android Gradle Plugin 8.1.4
- Kotlin 1.9.21
- All dependencies properly configured

✅ **Comprehensive Documentation**
- [QUICKSTART.md](QUICKSTART.md) - Get building in 5 minutes
- [BUILD.md](BUILD.md) - Detailed build instructions and troubleshooting
- [README.md](README.md) - Feature overview and project information
- [PRODUCTION_READINESS.md](PRODUCTION_READINESS.md) - Production deployment guide

✅ **Build Templates**
- `local.properties.template` - Template for SDK configuration

✅ **Git Configuration**
- `.gitignore` - Properly configured to exclude build artifacts
- `.gitattributes` - Ensures consistent line endings across platforms

## Quick Start

### 1. Clone This Branch

```bash
git clone -b copilot/create-complete-build-branch https://github.com/Liamhigh/Liam-Highcock.git
cd Liam-Highcock
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Click "Open"
3. Select the cloned directory
4. Wait for Gradle sync

### 3. Build

**Via Android Studio:**
- Menu: Build > Build Bundle(s) / APK(s) > Build APK(s)

**Via Command Line:**
```bash
./gradlew assembleDebug
```

That's it! See [QUICKSTART.md](QUICKSTART.md) for more details.

## System Requirements

### Required
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Java 17 (bundled with Android Studio)
- **Android SDK**: API 34 (installed via Android Studio SDK Manager)
- **Internet Connection**: For first-time dependency download
- **Disk Space**: ~5 GB for Android Studio + SDK + dependencies

### Supported Operating Systems
- Windows 10/11 (64-bit)
- macOS 10.14+ (Intel or Apple Silicon)
- Linux (64-bit Ubuntu, Fedora, Debian, etc.)

## What You Get

When you build this project, you'll get:

### Debug Build
- **Path**: `app/build/outputs/apk/debug/app-debug.apk`
- **Signing**: Automatically signed with debug keystore
- **Optimizations**: None (easier debugging)
- **Size**: ~15-20 MB
- **Use Case**: Development and testing

### Release Build
- **Path**: `app/build/outputs/apk/release/app-release.apk`
- **Signing**: Debug-signed by default (configure production signing in BUILD.md)
- **Optimizations**: ProGuard/R8 enabled
- **Size**: ~8-12 MB (minified)
- **Use Case**: Testing production builds

## Project Features

This is a complete forensic evidence collection application with:

- 📸 Document scanning with CameraX
- 🔐 Cryptographic sealing (SHA-512, HMAC-SHA512)
- 📍 GPS location capture
- 📄 PDF report generation
- 🔍 B1-B9 forensic analysis engine
- 💾 Room database for evidence storage
- 🔒 Offline-first design (no network required)

For full feature details, see [README.md](README.md).

## Dependencies

All dependencies will be automatically downloaded during the first Gradle sync:

### Core Android
- AndroidX Core, AppCompat, Material Components
- ConstraintLayout, Activity/Fragment KTX

### Features
- CameraX (camera capture)
- iText7 (PDF generation)
- Room (database)
- Security Crypto (encryption)
- Google Play Services Location (GPS)
- ZXing (QR codes)
- Kotlin Coroutines (async operations)

See `app/build.gradle.kts` for complete dependency list.

## Build Variants

The project supports two build variants:

1. **debug** - For development
   - No code minification
   - Debug logging enabled
   - Faster build times

2. **release** - For production
   - ProGuard/R8 code minification
   - Optimized APK
   - Production-ready

## Troubleshooting

### "SDK not found"
Solution: In Android Studio, go to Tools > SDK Manager and install:
- Android SDK Platform 34
- Build Tools 34.0.0

### "Gradle sync failed"
Solution: 
1. Check internet connection
2. File > Invalidate Caches > Invalidate and Restart
3. Try: `./gradlew clean --refresh-dependencies`

### "Java version mismatch"
Solution: Android Studio bundles Java 17. Ensure you're using it:
- File > Project Structure > SDK Location
- JDK Location should point to Android Studio's embedded JDK

### Build takes too long / runs out of memory
Solution: Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

For more troubleshooting, see [BUILD.md](BUILD.md).

## Network Requirements

### Initial Setup (One-time)
- Internet connection required to download:
  - Gradle dependencies
  - Android SDK components (if not already installed)
  - Android build tools

### After Setup
- **No internet required** for building
- **No internet required** for running the app
- The app is fully offline-capable

## File Structure

```
Liam-Highcock/
├── .git/                        # Git repository
├── .github/                     # GitHub Actions workflows
├── app/                         # Android application module
│   ├── build.gradle.kts        # App build configuration
│   ├── src/main/               # Source code
│   │   ├── java/               # Kotlin source files
│   │   ├── res/                # Resources (layouts, drawables, etc.)
│   │   └── AndroidManifest.xml # App manifest
│   └── proguard-rules.pro      # ProGuard configuration
├── gradle/                      # Gradle wrapper files
├── build.gradle.kts             # Root build configuration
├── settings.gradle.kts          # Project settings
├── gradle.properties            # Gradle properties
├── gradlew                      # Gradle wrapper (Unix)
├── .gitignore                   # Git ignore rules
├── .gitattributes               # Git attributes
├── local.properties.template    # Template for local config
├── QUICKSTART.md               # Quick start guide
├── BUILD.md                    # Detailed build instructions
├── README.md                   # Project overview
├── BRANCH_README.md            # This file
└── docs/                       # Additional documentation
```

## Next Steps After Building

1. **Test the App**: Run it on an emulator or physical device
2. **Read the Docs**: Check out [README.md](README.md) for features
3. **Customize**: Modify the source code as needed
4. **Deploy**: See [PRODUCTION_READINESS.md](PRODUCTION_READINESS.md)

## Getting Help

If you encounter issues:

1. Check [QUICKSTART.md](QUICKSTART.md)
2. Review [BUILD.md](BUILD.md) troubleshooting section
3. Verify all prerequisites are installed
4. Try a clean build: `./gradlew clean assembleDebug`
5. Open an issue on GitHub with details

## Contributing

See the main [README.md](README.md) for contribution guidelines.

## License

See [README.md](README.md) for license information.

---

**Ready to build?** Start with [QUICKSTART.md](QUICKSTART.md) for the fastest path to a working APK!
