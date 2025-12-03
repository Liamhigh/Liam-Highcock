# Repository Structure - Unified Codebase

This document provides a complete overview of the repository structure after merging all 29 branches.

```
Liam-Highcock/
├── .github/
│   └── workflows/
│       ├── android-build.yml          # Android CI workflow
│       └── build-apk.yml              # APK build workflow
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/verumomnis/forensic/
│   │   │   │   ├── core/
│   │   │   │   │   ├── ForensicEngine.kt
│   │   │   │   │   ├── ForensicEvidence.kt
│   │   │   │   │   ├── ForensicModels.kt           # NEW
│   │   │   │   │   └── VerumOmnisApplication.kt
│   │   │   │   │
│   │   │   │   ├── crypto/
│   │   │   │   │   └── CryptographicSealingEngine.kt
│   │   │   │   │
│   │   │   │   ├── database/
│   │   │   │   │   ├── ForensicDao.kt
│   │   │   │   │   ├── ForensicDatabase.kt
│   │   │   │   │   ├── ForensicEntities.kt
│   │   │   │   │   └── ForensicRepository.kt
│   │   │   │   │
│   │   │   │   ├── leveler/
│   │   │   │   │   └── LevelerEngine.kt           # B1-B9 Analysis
│   │   │   │   │
│   │   │   │   ├── location/
│   │   │   │   │   └── ForensicLocationService.kt
│   │   │   │   │
│   │   │   │   ├── metadata/
│   │   │   │   │   └── EvidenceMetadataExtractor.kt  # NEW
│   │   │   │   │
│   │   │   │   ├── pdf/
│   │   │   │   │   └── ForensicPdfGenerator.kt
│   │   │   │   │
│   │   │   │   ├── report/
│   │   │   │   │   └── ForensicNarrativeGenerator.kt
│   │   │   │   │
│   │   │   │   ├── tax/
│   │   │   │   │   └── TaxReturnEngine.kt
│   │   │   │   │
│   │   │   │   └── ui/
│   │   │   │       ├── AudioRecorderActivity.kt
│   │   │   │       ├── CaseDetailActivity.kt
│   │   │   │       ├── FileIntakeActivity.kt      # NEW
│   │   │   │       ├── MainActivity.kt
│   │   │   │       ├── ReportViewerActivity.kt
│   │   │   │       ├── ScannerActivity.kt
│   │   │   │       └── VideoRecorderActivity.kt
│   │   │   │
│   │   │   ├── assets/
│   │   │   │   └── forensic_rules.json            # NEW
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── gradient_overlay.xml       # NEW
│   │   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   │   ├── status_badge.xml           # NEW
│   │   │   │   │   └── status_badge_background.xml # NEW
│   │   │   │   │
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_case_detail.xml
│   │   │   │   │   ├── activity_file_intake.xml   # NEW
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_report_viewer.xml
│   │   │   │   │   ├── activity_scanner.xml
│   │   │   │   │   ├── dialog_new_case.xml        # NEW
│   │   │   │   │   ├── dialog_text_note.xml       # NEW
│   │   │   │   │   ├── item_case.xml
│   │   │   │   │   └── item_evidence.xml
│   │   │   │   │
│   │   │   │   ├── mipmap-*/                      # App icons (all densities)
│   │   │   │   │   ├── ic_launcher.png
│   │   │   │   │   └── ic_launcher_round.png
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   │
│   │   │   │   └── xml/
│   │   │   │       ├── data_extraction_rules.xml
│   │   │   │       └── file_paths.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/
│   │       └── java/org/verumomnis/forensic/
│   │           ├── CryptographicSealingEngineTest.kt  # NEW
│   │           └── ForensicEngineTest.kt
│   │
│   ├── build.gradle.kts                   # App build configuration
│   └── proguard-rules.pro                 # R8/ProGuard rules
│
├── docs/
│   ├── images/
│   │   ├── logo-2.png
│   │   ├── logo-3.png
│   │   └── main-logo.png
│   │
│   ├── pdfs/
│   │   ├── Android build .PDF
│   │   ├── So valuable .PDF
│   │   ├── Verum_Omnis_Full_Template_v5.1.1_Complete(2).PDF
│   │   └── Verum_Omnis_Ideal_Logic_With_Brains (1)(1)(1).PDF
│   │
│   ├── ARCHITECTURE.md                    # System architecture
│   ├── DEPLOYMENT_GUIDE.md                # Deployment guide
│   ├── INSTALLATION_GUIDE.md              # Installation guide
│   └── USER_MANUAL.md                     # User manual
│
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
├── scripts/
│   ├── build-android.sh                   # Build script
│   ├── generate-assets.py                 # Asset generator
│   └── test-scanning-logic.kts            # Test script
│
├── .gitignore                             # Git ignore rules
├── build.gradle.kts                       # Root build configuration
├── CONTRIBUTING.md                        # Contribution guidelines
├── gradle.properties                      # Gradle properties
├── gradlew                                # Gradle wrapper (Unix)
├── gradlew.bat                            # Gradle wrapper (Windows)
├── MERGE_SUMMARY.md                       # This merge summary
├── PRODUCTION_READINESS.md                # Production readiness
├── README.md                              # Project README
├── REPOSITORY_STRUCTURE.md                # This file
├── settings.gradle.kts                    # Gradle settings
└── TODO.md                                # Future tasks
```

## Component Overview

### Core Application (22 Kotlin Files)

#### Business Logic Layer
- **ForensicEngine** - Central forensic evidence processing engine
- **LevelerEngine** - B1-B9 analysis (9 forensic analysis modules)
- **TaxReturnEngine** - Tax return processing (50% cheaper than alternatives)
- **CryptographicSealingEngine** - SHA-512 and HMAC-SHA512 cryptographic sealing

#### Data Layer
- **Room Database** - SQLite-based persistence
  - ForensicDao - Data access object
  - ForensicDatabase - Database configuration
  - ForensicEntities - Entity classes
  - ForensicRepository - Repository pattern
- **ForensicEvidence** - Evidence data models
- **ForensicModels** - Additional data models

#### Services Layer
- **ForensicLocationService** - GPS location capture
- **EvidenceMetadataExtractor** - EXIF and metadata extraction

#### Presentation Layer (7 Activities)
- **MainActivity** - App entry point, case management
- **ScannerActivity** - Document scanning with CameraX
- **FileIntakeActivity** - File import and management
- **AudioRecorderActivity** - Audio evidence recording
- **VideoRecorderActivity** - Video evidence recording
- **CaseDetailActivity** - Case details and evidence list
- **ReportViewerActivity** - PDF report viewer

#### Report Generation
- **ForensicPdfGenerator** - PDF generation with iText7
- **ForensicNarrativeGenerator** - Forensic narrative text generation

### Build System

#### Gradle Configuration
- **Android Gradle Plugin**: 8.2.0
- **Kotlin**: 1.9.20
- **Gradle**: 8.2
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

#### Key Dependencies
```gradle
// AndroidX
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.11.0")

// CameraX
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")

// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// PDF Generation
implementation("com.itextpdf:itext7-core:7.2.5")

// QR Code
implementation("com.google.zxing:core:3.5.2")

// Logging
implementation("org.slf4j:slf4j-api:2.0.9")
implementation("com.github.tony19:logback-android:3.0.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

### Documentation

#### User Documentation
1. **USER_MANUAL.md** - Complete user guide with screenshots
2. **INSTALLATION_GUIDE.md** - Step-by-step installation
3. **DEPLOYMENT_GUIDE.md** - Deployment procedures

#### Developer Documentation
1. **ARCHITECTURE.md** - System design and architecture
2. **CONTRIBUTING.md** - Contribution guidelines
3. **README.md** - Project overview and quick start
4. **TODO.md** - Future enhancements and roadmap

#### Merge Documentation
1. **MERGE_SUMMARY.md** - Complete merge summary
2. **REPOSITORY_STRUCTURE.md** - This file

### CI/CD

#### GitHub Actions Workflows
1. **android-build.yml** - Continuous integration
   - Builds on push/PR to main and develop
   - Runs unit tests
   - Uploads APK artifacts

2. **build-apk.yml** - APK building
   - Debug APK (unsigned)
   - Release APK (signed with secrets)

### Resources

#### Application Assets
- **forensic_rules.json** - Forensic analysis rules configuration

#### Android Resources
- **Layouts**: 10 XML files (activities, dialogs, items)
- **Drawables**: Icons, gradients, badges
- **Mipmaps**: Launcher icons (5 densities)
- **Values**: Colors, strings, themes
- **XML**: Data extraction rules, file paths

## Key Features

### Forensic Capabilities
✅ Document scanning with CameraX  
✅ Photo capture with metadata  
✅ Audio recording  
✅ Video recording  
✅ GPS location tagging  
✅ EXIF metadata extraction  
✅ Cryptographic sealing (SHA-512, HMAC-SHA512)  
✅ B1-B9 Leveler analysis  
✅ PDF report generation  
✅ QR code verification  
✅ Database persistence  
✅ File import/export  

### Tax Features
✅ Tax return processing (50% cheaper)  
✅ Tax document analysis  
✅ Tax evidence correlation  

### Security Features
✅ Offline-first architecture  
✅ No telemetry or external data transmission  
✅ Cryptographic evidence sealing  
✅ Tamper detection  
✅ Chain of custody tracking  

### Constitutional Compliance
✅ Truth - Factual, unmodified evidence  
✅ Fairness - Protect vulnerable parties  
✅ Human Rights - Dignity, equality, agency  
✅ Non-Extraction - No sensitive data transmission  
✅ Human Authority - AI assists, never overrides  
✅ Integrity - No manipulation or bias  
✅ Independence - No external influence  

## Testing

### Unit Tests
- **ForensicEngineTest.kt** - Core engine tests
- **CryptographicSealingEngineTest.kt** - Crypto tests

### Test Coverage
- Cryptographic operations
- Evidence sealing/verification
- Database operations
- Leveler analysis

## Build Variants

### Debug
- Unsigned APK
- Full debug symbols
- No minification
- Development logging

### Release
- Signed APK (via GitHub Secrets)
- R8 minification enabled
- ProGuard rules applied
- Production logging

## Next Steps

1. ✅ Merge all branches (COMPLETE)
2. ⏳ Build verification
3. ⏳ Run test suite
4. ⏳ Code review
5. ⏳ Security audit
6. ⏳ Production deployment

---

**Updated**: 2025-12-03  
**Repository**: Liamhigh/Liam-Highcock  
**Branch**: copilot/merge-all-branches-into-main
