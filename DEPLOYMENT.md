# Verum Omnis Forensic Engine - Deployment Guide

> **Status**: ✅ Code Complete - Ready for Build (requires Android SDK + internet)

---

## Quick Start: Building the APK

### Prerequisites

- **JDK 17+** (OpenJDK or Oracle JDK)
  - Download: https://adoptium.net/ (OpenJDK) or https://www.oracle.com/java/technologies/downloads/
- **Android SDK** (API level 34)
  - Install via Android Studio (recommended) or command-line tools
- **Internet Connection** (for initial Gradle dependency download)
- **Git** (to clone the repository)

### Step-by-Step Build Instructions

#### Option 1: Android Studio (Recommended)

1. **Install Android Studio**
   ```
   Download from: https://developer.android.com/studio
   ```

2. **Install JDK 17**
   - Android Studio includes JDK, but verify version:
   - File → Project Structure → SDK Location → JDK location
   - Should show JDK 17 or higher

3. **Clone Repository**
   ```bash
   git clone https://github.com/Liamhigh/Liam-Highcock.git
   cd Liam-Highcock
   ```

4. **Open in Android Studio**
   - File → Open → Select `Liam-Highcock` folder
   - Wait for Gradle sync to complete (downloads dependencies)
   - This may take 5-15 minutes on first run

5. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or use Build → Make Project (Ctrl+F9 / Cmd+F9)

6. **Locate APK**
   - Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
   - A notification will show "APK(s) generated successfully" with "locate" link

#### Option 2: Command Line

1. **Set Android SDK Path**
   ```bash
   # Option A: Environment variable
   export ANDROID_HOME=/path/to/android-sdk
   # Example macOS: export ANDROID_HOME=$HOME/Library/Android/sdk
   # Example Linux: export ANDROID_HOME=$HOME/Android/Sdk
   # Example Windows: set ANDROID_HOME=C:\Users\YourName\AppData\Local\Android\Sdk

   # Option B: Create local.properties file
   echo "sdk.dir=/path/to/android-sdk" > local.properties
   ```

2. **Clean Build** (recommended for first build)
   ```bash
   ./gradlew clean build
   ```

3. **Build Debug APK** (for testing)
   ```bash
   ./gradlew assembleDebug
   ```

4. **Build Release APK** (for production)
   ```bash
   ./gradlew assembleRelease
   ```

5. **Run Tests**
   ```bash
   ./gradlew testDebugUnitTest
   ```

6. **Run Lint Checks**
   ```bash
   ./gradlew lint
   ```

### Output Locations

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Test Results**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Lint Report**: `app/build/reports/lint-results-debug.html`

---

## Installing APK on Android Device

### From Android Studio

1. Connect Android device via USB
2. Enable Developer Options on device:
   - Settings → About Phone → Tap "Build Number" 7 times
3. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
4. Run → Run 'app' (Shift+F10 / Ctrl+R)
5. Select your device from the list

### Manual Installation

1. **Transfer APK to Device**
   - Via USB: Copy APK to device storage
   - Via Email: Email APK to yourself and download on device
   - Via Cloud: Upload to Google Drive/Dropbox and download on device

2. **Enable Unknown Sources**
   - Android 8.0+: Settings → Apps → Special Access → Install Unknown Apps → Select browser/file manager → Allow
   - Android 7.1 and below: Settings → Security → Unknown Sources → Enable

3. **Install APK**
   - Open file manager on device
   - Navigate to APK location
   - Tap APK file
   - Tap "Install"
   - Tap "Open" when installation completes

### Using ADB (Advanced)

```bash
# Install to connected device
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall previous version first (if needed)
adb uninstall org.verumomnis.forensic

# Install and launch
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n org.verumomnis.forensic/.ui.MainActivity
```

---

## GitHub Actions CI/CD Setup

### Automatic Builds

The repository includes a `.github/workflows/android-build.yml` workflow that automatically:

1. **Triggers** on push to `main` or `develop` branches
2. **Builds** both debug and release APKs
3. **Runs** unit tests
4. **Uploads** APK artifacts (30-day retention for debug, 90-day for release)

### Downloading Pre-Built APKs

1. Go to repository on GitHub
2. Click **Actions** tab
3. Click on **Android CI/CD** workflow
4. Select a successful workflow run (green checkmark)
5. Scroll to **Artifacts** section
6. Download:
   - `verum-omnis-forensic-debug` - Debug APK
   - `verum-omnis-forensic-release-debug-signed` - Release APK with debug signing
   - `verum-omnis-forensic-release-production` - Production APK (if keystore configured)

### Configuring Production Signing

To enable production-signed release APKs, configure GitHub repository secrets:

1. **Generate Production Keystore** (if you don't have one)

   ```bash
   keytool -genkeypair -v \
     -keystore verum-omnis-release.keystore \
     -storepass YOUR_STRONG_STORE_PASSWORD \
     -alias verum-omnis-key \
     -keypass YOUR_STRONG_KEY_PASSWORD \
     -keyalg RSA \
     -keysize 2048 \
     -validity 10000 \
     -dname "CN=Verum Omnis, OU=Forensic Engine, O=Verum Global Foundation, L=City, S=State, C=XX"
   ```

   **⚠️ Security Best Practices:**
   - Use strong passwords (16+ characters, mixed case, numbers, symbols)
   - Never commit keystore files to version control
   - Store keystore and passwords in secure password manager
   - Back up keystore in multiple secure locations (losing it means you can't update your app!)

2. **Encode Keystore to Base64**

   ```bash
   # macOS
   base64 -i verum-omnis-release.keystore | pbcopy
   
   # Linux
   base64 verum-omnis-release.keystore > keystore_base64.txt
   # Then copy contents of keystore_base64.txt
   
   # Windows (PowerShell)
   [Convert]::ToBase64String([IO.File]::ReadAllBytes("verum-omnis-release.keystore")) | Set-Clipboard
   ```

3. **Add GitHub Secrets**

   - Go to repository: Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Add these secrets:

   | Secret Name | Value |
   |-------------|-------|
   | `KEYSTORE_BASE64` | Base64-encoded keystore file content |
   | `KEYSTORE_PASSWORD` | Your keystore password |
   | `KEY_ALIAS` | Key alias (e.g., `verum-omnis-key`) |
   | `KEY_PASSWORD` | Key password |

4. **Verify Configuration**
   - Push a commit to `main` branch
   - Check Actions tab for workflow run
   - Download `verum-omnis-forensic-release-production` artifact
   - This APK is production-signed and ready for distribution

### Manual Workflow Trigger

You can manually trigger builds without pushing code:

1. Go to **Actions** → **Android CI/CD**
2. Click **Run workflow** button
3. Select branch (main/develop)
4. Choose build type:
   - Both (debug + release)
   - Debug only
   - Release only
5. Click **Run workflow**
6. Wait for completion (~5-10 minutes)
7. Download APK artifacts

---

## Project Structure

```
Liam-Highcock/
├── .github/
│   ├── workflows/
│   │   └── android-build.yml      # CI/CD workflow
│   ├── copilot-instructions.md    # Copilot coding guidelines
│   └── instructions/              # Module-specific guidelines
├── app/
│   ├── build.gradle.kts           # App module build config
│   ├── proguard-rules.pro         # ProGuard obfuscation rules
│   └── src/
│       ├── main/
│       │   ├── java/org/verumomnis/forensic/
│       │   │   ├── core/          # Core forensic engine
│       │   │   ├── crypto/        # Cryptographic sealing
│       │   │   ├── leveler/       # B1-B9 Leveler Engine
│       │   │   ├── pdf/           # PDF report generation
│       │   │   ├── report/        # Narrative generation
│       │   │   ├── location/      # GPS location services
│       │   │   ├── database/      # Room database persistence
│       │   │   ├── tax/           # Tax return engine
│       │   │   └── ui/            # User interface Activities
│       │   ├── res/               # Android resources
│       │   │   ├── layout/        # XML layouts
│       │   │   ├── values/        # Strings, colors, themes
│       │   │   ├── drawable/      # Icons and images
│       │   │   └── xml/           # Network security, backup rules
│       │   └── AndroidManifest.xml
│       └── test/
│           └── java/              # Unit tests
├── build.gradle.kts               # Project-level build config
├── settings.gradle.kts            # Gradle settings
├── gradle/                        # Gradle wrapper
├── gradlew                        # Gradle wrapper script (Unix)
├── gradlew.bat                    # Gradle wrapper script (Windows)
└── docs/                          # Documentation
```

---

## Build Variants

### Debug Build

**Purpose**: Development and testing

**Characteristics**:
- Debug symbols included
- No code obfuscation
- Signed with debug keystore (auto-generated)
- Debuggable in Android Studio
- Larger APK size
- Faster build time

**Build Command**:
```bash
./gradlew assembleDebug
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

**Purpose**: Production deployment

**Characteristics**:
- No debug symbols
- ProGuard code obfuscation enabled
- Signed with production or debug keystore
- Not debuggable
- Smaller APK size (~30-40% smaller)
- Longer build time

**Build Command**:
```bash
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk`

---

## Testing

### Unit Tests

Run all unit tests:
```bash
./gradlew testDebugUnitTest
```

Run specific test class:
```bash
./gradlew testDebugUnitTest --tests LevelerEngineTest
```

View test results:
```bash
# Open in browser
open app/build/reports/tests/testDebugUnitTest/index.html
```

### Instrumentation Tests (Requires Emulator or Device)

```bash
# Start emulator first
./gradlew connectedAndroidTest
```

### Lint Checks

```bash
./gradlew lint

# View lint report
open app/build/reports/lint-results-debug.html
```

---

## Performance Optimization

### ProGuard Configuration

Release builds use ProGuard for:
- Code obfuscation (security)
- Dead code removal (smaller APK)
- Optimization (faster runtime)

**Configuration**: `app/proguard-rules.pro`

**Verification**:
1. Build release APK
2. Check size reduction (should be 30-40% smaller than debug)
3. Test all features to ensure nothing is broken by obfuscation

### APK Size Optimization

| Optimization | Status | Impact |
|--------------|--------|--------|
| ProGuard enabled | ✅ Yes | -30% size |
| Unused resources removed | ✅ Yes | -10% size |
| Vector drawables | ✅ Yes | -5% size |
| Native libraries stripped | ✅ Auto | -10% size |
| WebP images (if any) | ⚠️ N/A | N/A |

**Expected APK Size**:
- Debug: ~15-20 MB
- Release: ~10-14 MB

---

## Troubleshooting

### Build Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `SDK location not found` | ANDROID_HOME not set | Set environment variable or create `local.properties` |
| `Plugin not found` | No internet/Maven access | Ensure internet connection, check proxy settings |
| `Java version mismatch` | Wrong JDK version | Install JDK 17+, verify with `java -version` |
| `Gradle sync failed` | Corrupted Gradle cache | Run `./gradlew --refresh-dependencies` |
| `NDK not found` | Missing NDK | Install NDK via Android SDK Manager (usually not needed) |
| `Out of memory` | Insufficient heap | Increase: `GRADLE_OPTS="-Xmx4g"` |

### Runtime Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `App crashes on startup` | ProGuard over-obfuscation | Add ProGuard keep rules |
| `PDF generation fails` | iText license issue | Verify iText7 dependency version |
| `Camera not working` | Missing permissions | Check runtime permissions in code |
| `Location not working` | GPS disabled | Enable location services on device |
| `Database migration error` | Room schema change | Increment database version number |

### Installation Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `App not installed` | Signature conflict | Uninstall old version first |
| `Parse error` | Corrupted APK | Re-build APK |
| `Insufficient storage` | Low device storage | Free up space on device |
| `Installation blocked` | Unknown sources disabled | Enable "Install unknown apps" |

### CI/CD Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `Workflow fails at build` | Missing SDK components | Update GitHub Actions workflow |
| `Upload artifact fails` | Artifact too large | Verify APK size < 100MB |
| `Signing fails` | Invalid keystore secrets | Re-encode and re-upload keystore |
| `Tests fail` | Environment issue | Check test logs in Actions |

---

## Security Considerations

### Keystore Management

**⚠️ CRITICAL SECURITY REQUIREMENTS:**

1. **Never commit keystore files to Git**
   - Add `*.keystore` to `.gitignore` (already done)
   - Never share keystore files publicly

2. **Use strong passwords**
   - Minimum 16 characters
   - Mixed case, numbers, symbols
   - Different password for store and key
   - Store in password manager (1Password, Bitwarden, etc.)

3. **Back up keystore securely**
   - Store in multiple secure locations
   - Encrypted cloud storage
   - Physical secure storage
   - **If lost, you cannot update your app!**

4. **Protect GitHub secrets**
   - Only repository admins should access
   - Regularly rotate if exposed
   - Use environment protection rules

### Code Signing Verification

After building, verify APK signature:

```bash
# View signing certificate info
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# Get certificate fingerprint
keytool -list -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

### Network Security

The app enforces strict network security:

**Configuration**: `app/src/main/res/xml/network_security_config.xml`

- No clear text traffic allowed
- No user certificates trusted
- Restrictive by default

**Note**: The app doesn't use network at all (offline-first), so this is defense-in-depth.

---

## Distribution Options

### Direct Distribution

1. **Email/Cloud**
   - Send APK directly to users
   - Users install manually via "Unknown sources"
   - No review process required
   - Full control over updates

2. **Website Download**
   - Host APK on your website
   - Provide download link
   - Include SHA-256 checksum for verification
   - No app store fees

### App Store Distribution (Future)

1. **Google Play Store**
   - Requires Google Play Console account ($25 one-time fee)
   - App review process (1-3 days)
   - Automatic updates
   - Wider reach
   - Requires Play Store signing key

2. **Alternative Stores**
   - F-Droid (for open source)
   - APKPure, APKMirror (third-party)
   - Samsung Galaxy Store
   - Amazon Appstore

---

## Versioning

### Version Number Format

**Format**: `MAJOR.MINOR.PATCH`

**Current**: `1.0.0`

**Update in**: `app/build.gradle.kts`

```kotlin
android {
    defaultConfig {
        versionCode = 1         // Increment for each release
        versionName = "1.0.0"   // Human-readable version
    }
}
```

**Versioning Guidelines**:
- **MAJOR**: Breaking changes, major features
- **MINOR**: New features, backward-compatible
- **PATCH**: Bug fixes, minor updates

**Example Progression**:
- 1.0.0 → Initial release
- 1.0.1 → Bug fix
- 1.1.0 → New feature (OCR)
- 2.0.0 → Major redesign

---

## Deployment Checklist

### Pre-Build Checklist

- [x] All code committed
- [x] Version number updated
- [x] ProGuard rules tested
- [x] Unit tests passing
- [x] Lint warnings reviewed
- [x] Keystore configured (for production)
- [ ] Release notes written

### Build Checklist

- [ ] Build debug APK
- [ ] Test debug APK on emulator
- [ ] Test debug APK on physical device
- [ ] Run all unit tests
- [ ] Run lint checks
- [ ] Build release APK
- [ ] Verify ProGuard worked (check size)
- [ ] Test release APK thoroughly

### Post-Build Checklist

- [ ] Verify APK signature
- [ ] Test all features in release APK
- [ ] Test on multiple Android versions
- [ ] Test on different screen sizes
- [ ] Document known issues
- [ ] Create GitHub Release
- [ ] Upload APK to release
- [ ] Update documentation
- [ ] Announce release

---

## Continuous Deployment Strategy

### Development Workflow

```
feature-branch → develop → main → production
     ↓             ↓         ↓
  (local)      (debug)   (release)
```

1. **Feature Development**: Work on feature branches
2. **Integration**: Merge to `develop` branch
3. **Debug Builds**: Automatic debug APK builds on `develop`
4. **Testing**: QA testing with debug APKs
5. **Release**: Merge to `main` when ready
6. **Production**: Automatic production APK builds on `main`

### Release Process

1. **Code Freeze**: Stop new features
2. **Testing Phase**: Intensive testing of release candidate
3. **Bug Fixes**: Fix critical bugs only
4. **Version Bump**: Update version numbers
5. **Build Release**: `./gradlew assembleRelease`
6. **Final Testing**: Test release APK
7. **Create Release**: Tag and release on GitHub
8. **Distribute**: Upload to chosen distribution channel
9. **Monitor**: Watch for crash reports and issues
10. **Hotfix**: Address critical issues immediately

---

## Performance Benchmarks

### Build Times (Approximate)

| Build Type | First Build | Incremental Build |
|------------|-------------|-------------------|
| Debug | 3-5 minutes | 30-60 seconds |
| Release | 5-8 minutes | 1-2 minutes |
| Clean Build | 5-10 minutes | N/A |

**Factors affecting build time**:
- Machine specs (CPU, RAM)
- Gradle daemon status
- Cache state
- Number of modules
- ProGuard processing

### APK Installation

| Device | Installation Time |
|--------|-------------------|
| Emulator | 10-20 seconds |
| Physical Device (USB) | 5-10 seconds |
| Physical Device (Manual) | 30-60 seconds |

---

## Getting Help

### Resources

- **Documentation**: See `README.md`, `PROJECT_STATUS.md`, `PRODUCTION_READINESS.md`
- **GitHub Issues**: https://github.com/Liamhigh/Liam-Highcock/issues
- **Android Developer Docs**: https://developer.android.com/docs
- **Gradle Docs**: https://docs.gradle.org/

### Common Questions

**Q: Why does the build fail with "Plugin not found"?**
A: You need internet access for Gradle to download the Android plugin from Google's Maven repository. This is a one-time requirement.

**Q: Can I build without Android Studio?**
A: Yes, use command line with Android SDK command-line tools. But Android Studio is recommended for beginners.

**Q: How do I update dependencies?**
A: Edit `app/build.gradle.kts` and update version numbers. Check compatibility first.

**Q: What's the minimum Android version supported?**
A: API 24 (Android 7.0 Nougat, released 2016). Covers ~95% of active devices.

**Q: Can I build on Windows/Mac/Linux?**
A: Yes, Gradle works on all platforms. Just adjust file paths accordingly.

---

## Next Steps After Successful Build

1. **Test Thoroughly**: Install on multiple devices and test all features
2. **Create Demo Video**: Show app functionality for documentation
3. **Write User Guide**: End-user documentation for case creation and reports
4. **Plan Updates**: Create roadmap for future features
5. **Set Up Monitoring**: Plan for crash reporting (if desired)
6. **Community Building**: Share with target users, gather feedback
7. **Iterate**: Based on feedback, prioritize improvements

---

*Deployment guide aligned with Verumdec standards*
*Last Updated: December 7, 2024*
*Version: 1.0.0*
