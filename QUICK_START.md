# Verum Omnis Forensic Engine - Quick Start Guide

*Get up and running in under 30 minutes*

---

## 🎯 What You're Building

The **Verum Omnis Forensic Engine** is a privacy-first Android application for:
- 📸 Collecting forensic evidence (documents, photos, audio, video, text)
- 🔐 Cryptographically sealing evidence with SHA-512 + HMAC-SHA512
- 📊 Analyzing evidence with the B1-B9 Leveler Engine
- 📄 Generating legal-grade PDF reports
- 💰 Preparing tax returns (50% cheaper than accountants)

**100% Offline** • **No Telemetry** • **Airgap Ready**

---

## ⚡ Quick Start (3 Steps)

### Step 1: Install Android Studio (5 minutes)

**Download**: https://developer.android.com/studio

**Why**: Includes everything you need (JDK, Android SDK, emulator)

**Installation**:
- macOS: Drag to Applications
- Windows: Run installer
- Linux: Extract and run `studio.sh`

### Step 2: Clone & Open Project (2 minutes)

```bash
git clone https://github.com/Liamhigh/Liam-Highcock.git
cd Liam-Highcock
```

**In Android Studio**:
1. File → Open
2. Select `Liam-Highcock` folder
3. Click "Trust Project"
4. Wait for Gradle sync (5-10 minutes first time)

### Step 3: Build & Run (5 minutes)

**On Emulator**:
1. Tools → Device Manager
2. Click "Create Device"
3. Select Pixel 7 (or any device)
4. Select system image: API 34 (Android 14)
5. Click "Finish"
6. Click ▶️ Run button (or Shift+F10)

**On Physical Device**:
1. Enable Developer Options on phone
2. Enable USB Debugging
3. Connect via USB
4. Click ▶️ Run button
5. Select your device

**Success**: App launches showing "Verum Omnis Forensic Engine" main screen

---

## 🚀 Your First Forensic Case (5 minutes)

### 1. Create a Case

- Tap **"New Case"** button
- Enter case name: "Test Case 1"
- Enter description: "Testing forensic features"
- Tap **"Create"**

### 2. Add Evidence

- Tap on your case in the list
- Tap **"Add Evidence"** button
- Select **"Text Note"**
- Enter: "Party A claims payment was made on Jan 15, 2024"
- Tap **"Add Evidence"** again
- Enter: "Party A later states payment was made on Jan 20, 2024"
- Tap **"Add Evidence"** again
- Enter: "Bank statement shows no payment on either date"

### 3. Generate Report

- Tap **"Generate Report"** button
- Wait 5-10 seconds for analysis
- Report automatically opens showing:
  - **B2 Contradiction**: Detected date discrepancy (Jan 15 vs Jan 20)
  - **B2 Contradiction**: Payment claim vs bank evidence
  - **B9 Integrity Score**: Calculated based on contradictions
  - **PDF Report**: Legal-grade sealed report

### 4. View Cryptographic Seal

- Scroll to bottom of report
- See **SHA-512 Case Hash** (128-character hex string)
- This proves report integrity and tamper detection

**🎉 Congratulations! You've created your first forensic case.**

---

## 📱 Testing All Evidence Types

### Document Scan

1. Tap case → **"Add Evidence"** → **"Scan Document"**
2. Point camera at a document
3. Tap capture button
4. Evidence saved with photo and GPS location

### Photo Capture

1. Tap case → **"Add Evidence"** → **"Take Photo"**
2. Point camera at scene
3. Tap capture button
4. Evidence saved with timestamp and location

### Audio Recording

1. Tap case → **"Add Evidence"** → **"Record Audio"**
2. Tap record button
3. Speak your evidence statement
4. Tap stop button
5. Audio saved with duration and timestamp

### Video Recording

1. Tap case → **"Add Evidence"** → **"Record Video"**
2. Tap record button
3. Record your video evidence
4. Tap stop button
5. Video saved with duration and timestamp

---

## 🔬 Testing B1-B9 Leveler Engine

Create a case with these statements to see all analysis modules:

**Evidence Set for Testing**:

1. **Timeline Contradiction (B1, B4)**:
   - "Meeting occurred on Monday, March 1st"
   - "Meeting occurred on Tuesday, March 1st"
   - (March 1st can't be both Monday and Tuesday)

2. **Financial Contradiction (B6)**:
   - "I paid $5,000 for the service"
   - "Bank statement shows $3,000 payment"
   - (Amount discrepancy)

3. **Behavioral Pattern (B5)**:
   - "I never received any emails about this"
   - [Add screenshot of email showing multiple messages]
   - (Evasion pattern detected)

4. **Missing Evidence (B3)**:
   - "Contract was signed and notarized"
   - [No contract document provided]
   - (Critical gap detected)

**Generate Report** → All B1-B9 modules will activate and provide analysis

---

## 💰 Testing Tax Return Engine

### Individual Tax Return (UK Example)

1. Create new case: "2024 Tax Return"
2. Add evidence: "Employment income: £45,000"
3. Add evidence: "Pension contributions: £5,000"
4. Add evidence: "Charitable donations: £500"
5. Generate report
6. Report shows:
   - Tax calculation: £45,000 income
   - Deductions optimized
   - Tax liability calculated
   - **Pricing**: £125 (vs £250 typical accountant fee = 50% savings)

### Test Other Jurisdictions

- **UAE**: "Income: AED 180,000"
- **US**: "Income: $65,000"
- **EU**: "Income: €50,000"

Engine automatically detects currency and applies correct tax rules.

---

## 🔍 Verifying Security Features

### Cryptographic Sealing

1. Generate a report
2. Note the SHA-512 hash at bottom
3. Make ANY change to the case (add/remove evidence)
4. Generate report again
5. Note the hash is COMPLETELY DIFFERENT
6. This proves tamper detection works

### Offline Operation

1. Enable Airplane Mode on device
2. Create new case
3. Add all evidence types
4. Generate report
5. Everything works offline ✅
6. No data transmitted to internet ✅

### No Telemetry

1. Monitor network traffic (e.g., with Charles Proxy)
2. Use app normally
3. No network requests detected ✅
4. Complete privacy guaranteed ✅

---

## 🛠️ Building APK for Distribution

### Debug APK (for testing)

```bash
./gradlew assembleDebug
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

**Size**: ~15-20 MB

**Use**: Testing on other devices

### Release APK (for production)

```bash
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk`

**Size**: ~10-14 MB (ProGuard optimized)

**Use**: Production distribution

### Installing APK

**Via ADB**:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Manually**:
1. Copy APK to phone
2. Enable "Install from Unknown Sources"
3. Tap APK to install

---

## 📚 Learning More

### Documentation

- **README.md**: Complete feature overview
- **DEPLOYMENT.md**: Detailed build instructions
- **PROJECT_STATUS.md**: Production readiness assessment
- **CHECKLIST.md**: Implementation tracking
- **ARCHITECTURE.md**: Technical architecture

### Code Structure

```
app/src/main/java/org/verumomnis/forensic/
├── core/          ← Start here (ForensicEngine.kt)
├── crypto/        ← Cryptographic sealing
├── leveler/       ← B1-B9 analysis engine
├── pdf/           ← Report generation
├── ui/            ← User interface
└── database/      ← Data persistence
```

### Key Files to Understand

1. **ForensicEngine.kt**: Core case management
2. **CryptographicSealingEngine.kt**: SHA-512 + HMAC
3. **LevelerEngine.kt**: B1-B9 analysis
4. **ForensicPdfGenerator.kt**: PDF reports
5. **MainActivity.kt**: UI entry point

---

## 🐛 Troubleshooting

### "Gradle Sync Failed"

**Solution**: Wait and retry. First sync downloads ~500MB of dependencies.

### "SDK Not Found"

**Solution**: 
```bash
# Create local.properties
echo "sdk.dir=/path/to/Android/sdk" > local.properties
```

Or in Android Studio: File → Project Structure → SDK Location

### "Cannot Resolve Symbol"

**Solution**: File → Invalidate Caches → Invalidate and Restart

### "Build Failed"

**Solution**: 
```bash
./gradlew clean build --refresh-dependencies
```

### "App Crashes on Launch"

**Solution**: Check logcat in Android Studio (View → Tool Windows → Logcat)

### "Tests Don't Run"

**Solution**: 
```bash
./gradlew testDebugUnitTest --rerun-tasks
```

---

## ✅ Quick Verification Checklist

After setup, verify these work:

- [ ] App launches successfully
- [ ] Can create new case
- [ ] Can add text evidence
- [ ] Can scan document (camera works)
- [ ] Can record audio
- [ ] Can record video
- [ ] Can generate PDF report
- [ ] Report shows B1-B9 analysis
- [ ] Report shows SHA-512 hash
- [ ] App works in airplane mode (offline)
- [ ] Can export case to JSON
- [ ] Can view report in report viewer

**All checked?** 🎉 **You're production ready!**

---

## 🚀 Next Steps

### For Development

1. **Modify UI**: Edit XML layouts in `app/src/main/res/layout/`
2. **Add Features**: Extend existing engines in `core/`, `leveler/`, etc.
3. **Add Tests**: Create tests in `app/src/test/java/`
4. **Customize**: Modify colors, strings, themes in `res/values/`

### For Distribution

1. **Generate Production Keystore**:
   ```bash
   keytool -genkeypair -v -keystore release.keystore \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias app-key
   ```

2. **Build Signed Release APK**:
   ```bash
   ./gradlew assembleRelease
   ```

3. **Distribute**:
   - Upload to GitHub Releases
   - Share directly with users
   - Submit to Google Play (optional)

### For Learning

1. **Study B1-B9 Leveler**: Read `leveler/LevelerEngine.kt`
2. **Understand Cryptography**: Read `crypto/CryptographicSealingEngine.kt`
3. **Learn PDF Generation**: Read `pdf/ForensicPdfGenerator.kt`
4. **Explore Database**: Read `database/ForensicDatabase.kt`

---

## 💡 Pro Tips

1. **Use Real Data**: Test with actual documents for realistic scenarios
2. **Test Contradictions**: Create intentionally contradictory evidence to see B2 in action
3. **Monitor Performance**: Use Android Profiler to check memory/CPU usage
4. **Export Cases**: Use JSON export to backup important cases
5. **Verify Hashes**: Always verify SHA-512 matches after report generation

---

## 📞 Getting Help

- **GitHub Issues**: https://github.com/Liamhigh/Liam-Highcock/issues
- **Documentation**: See docs/ folder
- **Android Docs**: https://developer.android.com/docs
- **Kotlin Docs**: https://kotlinlang.org/docs/

---

## 🎯 Success Criteria

You've successfully set up the Verum Omnis Forensic Engine when:

✅ App builds without errors
✅ App runs on emulator or device
✅ Can create cases and add evidence
✅ Can generate sealed PDF reports
✅ B1-B9 analysis produces results
✅ Cryptographic hashes are generated
✅ App works completely offline

**Time to achieve**: ~30 minutes (with Android Studio)

---

**Welcome to Verum Omnis Forensic Engine!** 🎉

*AI Forensics for Truth*

---

*Quick Start Guide Version 1.0.0*
*Last Updated: December 7, 2024*
