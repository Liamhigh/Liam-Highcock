# Quick Start Guide - Verum Omnis Forensic Engine

Get building in 5 minutes!

## Step 1: Install Prerequisites

### Download and Install Android Studio
- Go to: https://developer.android.com/studio
- Download Android Studio Hedgehog (2023.1.1) or later
- Install following the wizard

### Verify Java 17
Android Studio bundles Java 17. You can verify:
```bash
# After installing Android Studio
java -version
# Should show version 17.x.x
```

## Step 2: Clone the Repository

```bash
git clone https://github.com/Liamhigh/Liam-Highcock.git
cd Liam-Highcock
git checkout copilot/create-complete-build-branch
```

## Step 3: Open in Android Studio

1. Launch Android Studio
2. Click **"Open"**
3. Select the `Liam-Highcock` folder you just cloned
4. Click **"OK"**
5. Wait for Gradle sync (3-10 minutes on first run)
   - Android Studio will download all dependencies automatically
   - You'll see progress in the bottom status bar

## Step 4: Build APK

### Option A: Using Android Studio GUI (Easiest)
1. Menu: **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Wait for build (2-5 minutes)
3. Click "locate" in the notification to find your APK
4. APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Option B: Using Command Line
```bash
# In the project directory
./gradlew assembleDebug
```

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## Step 5: Install on Android Device

### Via USB
1. Enable Developer Options on your phone:
   - Settings > About Phone > Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings > Developer Options > USB Debugging
3. Connect phone to computer
4. In Android Studio: **Run > Run 'app'**
5. Select your device from the list

### Via APK File
1. Copy `app-debug.apk` to your phone
2. On phone: Settings > Security > Enable "Install from Unknown Sources"
3. Open the APK file on your phone
4. Tap "Install"

## Done! 🎉

The app should now be installed on your device as "VerumOmnisForensic".

## Next Steps

- Read [README.md](README.md) for feature overview
- See [BUILD.md](BUILD.md) for detailed build options
- Check [PRODUCTION_READINESS.md](PRODUCTION_READINESS.md) for production deployment

## Troubleshooting

### Gradle Sync Failed
- Check internet connection
- In Android Studio: File > Invalidate Caches > Invalidate and Restart

### SDK Not Found
- Tools > SDK Manager
- Install "Android SDK Platform 34"
- Install "Build Tools 34.0.0"

### Build Failed
```bash
# Clean and rebuild
./gradlew clean assembleDebug
```

### Need More Help?
See [BUILD.md](BUILD.md) for comprehensive troubleshooting.

---

**Questions?** Check the full documentation in BUILD.md or open an issue on GitHub.
