# Verum Omnis Forensic Engine

![Verum Omnis Logo](main-logo.png)

**Offline Android Forensic Engine with Cryptographic Sealing**

An Android application for collecting, sealing, and reporting forensic evidence in accordance with the Verum Omnis Constitutional Governance Layer.

## 🎯 Features

- 📸 **Document Capture** - Camera-based evidence collection
- 🔐 **Cryptographic Sealing** - SHA-512 hashing with HMAC-SHA512 for tamper detection
- 📍 **GPS Location Capture** - Automatic geolocation of evidence at collection time
- 📄 **AI-Readable PDF Reports** - Structured forensic narratives following legal admissibility standards
- 🔒 **Offline-First Design** - No cloud logging, no telemetry, airgap ready
- 📊 **B1-B9 Leveler Compliance** - Complete contradiction detection and integrity scoring

## 🏛️ Constitutional Governance

This application operates under the **Verum Omnis Constitution Mode**, which enforces:

### Core Principles

| Principle | Description |
|-----------|-------------|
| **Truth** | Factual accuracy and verifiable evidence |
| **Fairness** | Protection of vulnerable parties |
| **Human Rights** | Dignity, equality, and agency |
| **Non-Extraction** | No sensitive data transmission |
| **Human Authority** | AI assists, never overrides |
| **Integrity** | No manipulation or bias |
| **Independence** | No external influence on outputs |

### Forensic Standards

| Standard | Value |
|----------|-------|
| Hash Standard | SHA-512 |
| PDF Standard | PDF 1.7 |
| Watermark | VERUM OMNIS FORENSIC SEAL |
| QR Code Inclusion | Yes |
| Tamper Detection | Mandatory |
| Admissibility Standard | Legal-grade |

### Security

| Feature | Status |
|---------|--------|
| Offline First | ✅ True |
| Stateless | ✅ True |
| No Cloud Logging | ✅ True |
| No Telemetry | ✅ True |
| Airgap Ready | ✅ True |

## 🚀 Building

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Build Debug APK

```bash
./gradlew assembleDebug
```

### Build Release APK

```bash
./gradlew assembleRelease
```

The APK will be output to `app/build/outputs/apk/`

### Download Signed APKs from GitHub Actions

You can download pre-built signed APKs directly from GitHub Actions:

1. Go to the **Actions** tab in this repository
2. Click on **Android CI/CD** workflow
3. Select a successful workflow run
4. Download the artifacts:
   - **verum-omnis-forensic-debug** - Debug APK (automatically signed with debug keystore)
   - **verum-omnis-forensic-release-debug-signed** - Release APK signed with debug keystore (for testing)
   - **verum-omnis-forensic-release-production** - Production release APK (when production keystore secrets are configured)

#### Configuring Production APK Signing

To enable production-signed release APKs, you need to configure the following GitHub repository secrets:

1. **Generate a keystore** (if you don't have one):
   ```bash
   keytool -genkeypair -v \
     -keystore my-release-key.keystore \
     -storepass YOUR_STORE_PASSWORD \
     -alias your-key-alias \
     -keypass YOUR_KEY_PASSWORD \
     -keyalg RSA \
     -keysize 2048 \
     -validity 10000 \
     -dname "CN=Your Name, OU=Your Unit, O=Your Org, L=City, S=State, C=XX"
   ```

2. **Encode the keystore to Base64**:
   ```bash
   base64 -i my-release-key.keystore | pbcopy  # macOS
   # or
   base64 my-release-key.keystore > keystore_base64.txt  # Linux
   ```

3. **Add the following secrets** in GitHub (Settings → Secrets and variables → Actions → New repository secret):
   | Secret Name | Description |
   |-------------|-------------|
   | `KEYSTORE_BASE64` | Base64-encoded keystore file content |
   | `KEYSTORE_PASSWORD` | Password for the keystore |
   | `KEY_ALIAS` | Alias of the key in the keystore |
   | `KEY_PASSWORD` | Password for the key |

Once configured, production-signed APKs will be automatically generated on pushes to the `main` branch.

#### Manual Workflow Trigger

You can also manually trigger a build to generate APKs:

1. Go to **Actions** → **Android CI/CD**
2. Click **Run workflow**
3. Select the build type (debug, release, or both)
4. Click **Run workflow**
5. Once complete, download the APK artifacts

#### Installing on Your Phone

1. Download the APK file from the workflow artifacts
2. On your Android phone, enable **Install from Unknown Sources** in Settings
3. Transfer the APK to your phone (via USB, email, or cloud storage)
4. Tap the APK file to install

## 📱 Usage

### Police Evidence Workflow

The app follows a structured workflow for forensic evidence collection:

```
1. CREATE CASE → 2. ADD EVIDENCE → 3. ANALYZE → 4. SEAL → 5. REPORT → 6. SAVE/SHARE → 7. VIEW
```

#### Step-by-Step Guide

1. **Create a Case** - Tap "+ New Case" and enter case name and description
2. **Add Evidence** - Choose one of the intake methods:
   - 📄 **Scan Document** - Use camera to capture document
   - 📷 **Take Photo** - Capture photo evidence
   - 📝 **Add Note** - Add text observations
   - 📂 **Import File** - Pick existing files from device (PDF, images, documents)
3. **Run Analysis** - Tap "Analyze" to run B1-B9 Leveler Engine analysis
4. **Seal Case** - Tap "Seal Case" to lock evidence and generate integrity hash
5. **Generate Report** - Tap "Report" to create the forensic PDF
6. **Save/Share** - Use "Save" or "Share" buttons to export the sealed report
7. **Verify** - Tap "Verify" to check case integrity hashes

### Evidence Types

- 📄 Documents (scanned or imported)
- 📷 Photos (captured or imported)
- 📝 Text (notes and observations)
- 📂 Files (PDF, Word, images from device)
- 🎤 Audio (coming soon)
- 🎬 Video (coming soon)

## 🔍 B1-B9 Leveler Engine

The Leveler Engine provides comprehensive evidence analysis:

| Code | Feature | Description |
|------|---------|-------------|
| B1 | Event Chronology | Timeline reconstruction from evidence |
| B2 | Contradiction Detection | Statement and evidence conflict identification |
| B3 | Evidence Gap Analysis | Missing evidence detection |
| B4 | Timeline Manipulation | Backdating and edit detection |
| B5 | Behavioral Patterns | Evasion, gaslighting, concealment detection |
| B6 | Financial Correlation | Transaction vs statement verification |
| B7 | Communication Analysis | Response patterns and deletions |
| B8 | Jurisdictional Compliance | UAE, UK, EU, US law checking |
| B9 | Integrity Scoring | 0-100 score with breakdown |

## 📁 Project Structure

```
app/src/main/java/org/verumomnis/forensic/
├── core/                    # Core forensic engine
│   ├── ForensicEngine.kt
│   ├── ForensicEvidence.kt
│   └── VerumOmnisApplication.kt
├── crypto/                  # Cryptographic sealing
│   └── CryptographicSealingEngine.kt
├── leveler/                 # B1-B9 Leveler Engine
│   └── LevelerEngine.kt
├── location/                # GPS location services
│   └── ForensicLocationService.kt
├── metadata/                # EXIF/metadata extraction
│   └── EvidenceMetadataExtractor.kt
├── pdf/                     # PDF report generation
│   └── ForensicPdfGenerator.kt
├── report/                  # Narrative generation
│   └── ForensicNarrativeGenerator.kt
└── ui/                      # User interface
    ├── MainActivity.kt
    ├── ScannerActivity.kt
    ├── CaseDetailActivity.kt
    ├── FileIntakeActivity.kt
    └── ReportViewerActivity.kt
```

## 🛡️ Security Considerations

- All evidence is cryptographically sealed at capture time
- SHA-512 hashes ensure content integrity verification
- HMAC-SHA512 provides tamper-proof sealing
- No data is transmitted to external servers
- All processing happens locally on device
- APK signature is included in reports for chain of trust

## 📋 Verification

Each forensic report includes:

1. **QR Code** - Contains report metadata and verification hashes
2. **APK Hash** - SHA-512 of the signing certificate
3. **Evidence Hashes** - Individual SHA-512 for each evidence item
4. **Case Integrity Hash** - Combined hash of all evidence
5. **Seal Hashes** - HMAC-SHA512 tamper-proof seals

## 🧪 End-to-End Testing

### Testing the App Flow

1. **Install the APK** on your Android device
2. **Launch the app** - Verify "Constitutional Governance: ACTIVE" is displayed
3. **Create a Test Case**:
   - Tap "+ New Case"
   - Enter name: "Test Evidence Case"
   - Enter description: "Testing forensic workflow"
   - Tap "Create"
4. **Add Evidence** (test all methods):
   - Tap case to open details
   - Tap "📄 Scan" → capture a document → verify "Evidence captured and sealed" message
   - Tap "📷 Photo" → capture a photo → verify sealing
   - Tap "📝 Note" → enter text → verify sealing
   - Tap "📂 Import File" → pick a file → verify sealing
5. **Run Analysis**:
   - Tap "Analyze" button
   - Verify B1-B9 analysis results dialog appears
   - Check Integrity Score is shown
6. **Seal Case**:
   - Tap "Seal Case" → confirm
   - Verify status changes to "SEALED"
   - Verify add evidence buttons are disabled
7. **Generate Report**:
   - Tap "Report"
   - Verify progress indicator appears
   - Verify Report Viewer opens with all data
8. **Save Report**:
   - Tap "Save"
   - Verify file saved message with path
9. **Share Report**:
   - Tap "Share"
   - Verify share sheet appears with PDF attachment
10. **Verify Integrity**:
    - Tap "Verify"
    - Verify "INTEGRITY VERIFIED" message appears

### Verifying Hashes

To independently verify evidence integrity:

1. **Export the report PDF** from the app
2. **Note the Content Hash** for each evidence item in the report
3. **Recalculate SHA-512** of original evidence content
4. **Compare hashes** - they must match exactly
5. **Verify Case Integrity Hash** matches the combined evidence chain

## 📜 License

Copyright © 2024 Verum Global Foundation

## 👤 Creator

**Liam Highcock**

---

*AI FORENSICS FOR TRUTH*
