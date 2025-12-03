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

### CI/CD Signed Release Builds

The GitHub Actions workflow automatically builds and signs release APKs. To enable signed builds, configure the following repository secrets:

| Secret | Description |
|--------|-------------|
| `KEYSTORE_BASE64` | Base64-encoded keystore file |
| `KEYSTORE_PASSWORD` | Password for the keystore |
| `KEY_ALIAS` | Alias of the signing key |
| `KEY_PASSWORD` | Password for the signing key |

To generate the base64-encoded keystore:
```bash
base64 -i your-keystore.jks -o keystore-base64.txt
```

Copy the contents of `keystore-base64.txt` to the `KEYSTORE_BASE64` secret.

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
