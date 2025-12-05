# Verum Forensic App

![Verum Omnis Logo](main-logo.png)

**Offline-First Android Forensic Toolkit**

The Verum Forensic App is an offline-first Android application designed to help users create, store, analyze, and seal evidence-based case files. It operates entirely on-device and performs forensic processing without sending data to external servers.

This README describes **what the app is supposed to do**, its **intended workflows**, and the **core modules** required for a functional build. Use this document as a guide for development, debugging, and implementation tasks.

---

## ❖ Core Purpose

The app allows a user to:

1. **Create a new forensic case** - Start with a case name and generate a unique case ID
2. **Add evidence** - Capture text notes, images, documents, voice notes, and scanned items
3. **Run forensic engine** - Process evidence locally with contradiction detection and narrative reconstruction
4. **Detect contradictions** - Analyze patterns, timelines, and behavioral indicators
5. **Compute integrity hash** - Generate SHA-512 case hash for tamper detection
6. **Generate sealed report** - Export or view reports with cryptographic sealing

The app is meant to function as a **self-contained forensic toolkit** that works completely offline.

---

## ❖ High-Level Features
### 1. Case Creation

- User enters a case name
- The app generates a unique case ID (format: `CASE-XXXXXXXX`)
- A new database entry is created with:
  ```
  - case.json (metadata: name, timestamp, UUID, description)
  - evidence/ (folder for evidence items)
  - reports/ (folder for generated reports)
  ```
- Metadata stored: case name, creation timestamp, modification timestamp, status

### 2. Evidence Capture

The user can add multiple types of evidence:

- **Text notes** - Manual text input saved as evidence items
- **Images** - Via camera scanner or gallery selection
- **Documents** - Scanned documents with CameraX
- **Audio recordings** - Voice notes (future enhancement)
- **Video recordings** - Video evidence (future enhancement)

Each piece of evidence is:
- Stored in the app's internal database
- Tagged with timestamp, location (if available), and device info
- Cryptographically hashed with SHA-512
- Sealed with HMAC-SHA512 when finalized

### 3. Forensic Engine

The local forensic engine performs comprehensive analysis:

- **Contradiction detection** - Identifies conflicting statements or evidence
- **Content analysis** - Processes text, images, and metadata
- **Timeline reconstruction** - Chronological ordering of events
- **Behavioral pattern detection** - Identifies evasion, gaslighting, concealment patterns
- **Summary generation** - Creates human-readable forensic narratives
- **SHA-512 hashing** - Computes integrity hash of all case data
- **Cryptographic sealing** - Packages and seals the output with tamper detection

The engine produces a structured result for display and export, including:
- B1-B9 Leveler analysis scores
- Integrity index (0-100)
- Contradiction matrix
- Timeline anomalies
- Jurisdictional compliance checks

### 4. Report Viewer

The report is shown inside the app and includes:

- **Case summary** - Name, ID, timestamps, status
- **Evidence index** - Complete list with hashes
- **Contradictions** - Detected conflicts and patterns
- **Narrative reconstruction** - AI-generated forensic summary
- **Timeline** - Chronological event sequence
- **SHA-512 verification** - Integrity hashes for verification
- **QR code** - Contains verification data
- **APK hash** - Chain of trust to app signature

Reports are:
- Saved to internal storage in PDF format
- Viewable within ReportViewerActivity
- Exportable for external use
- Cryptographically sealed and tamper-proof

### 5. Offline-First Design

**Critical Design Principle: Complete Offline Operation**

- **No external servers** - All processing happens on-device
- **No network calls** - App functions in airplane mode
- **Local database** - Room Database for persistence
- **File-based storage** - Internal storage for evidence and reports
- **Zero telemetry** - No analytics, crash reporting, or tracking
- **Airgap ready** - Designed for low-resource or zero-connectivity environments

This ensures:
- Data privacy and security
- Evidence integrity
- Legal admissibility
- Works in isolated environments

---

## ❖ Main Activities (Screens)

### **MainActivity**

**Purpose**: Entry point and case management hub

**Functionality**:
- User enters case name via dialog
- App initializes a new case with UUID
- Displays list of all cases (active, sealed, reported)
- Shows case count and status
- Navigates to **CaseDetailActivity** when case is selected

**UI Elements**:
- Case creation button
- RecyclerView of existing cases
- Each case card shows: ID, name, status, evidence count, last modified date
- Generate Report button per case (enabled when evidence exists)

### **CaseDetailActivity**

**Purpose**: Detailed case view and evidence management

**Functionality**:
- Shows case metadata (ID, name, description, timestamps, status)
- Lists all evidence items in the case
- Provides buttons to add different evidence types
- Allows running B1-B9 Leveler analysis
- Enables report generation
- Supports case sealing (locks from further edits)
- Displays integrity hash when sealed

**UI Elements**:
- Case header with metadata
- Evidence list (RecyclerView)
- Add Evidence buttons:
  - Add Document (opens ScannerActivity)
  - Add Photo (opens ScannerActivity)
  - Add Note (opens text input dialog)
  - Add Audio (opens AudioRecorderActivity)
  - Add Video (opens VideoRecorderActivity)
- Action buttons:
  - Run Analysis (B1-B9 Leveler)
  - Seal Case
  - Generate Report
  - Export Case

### **ScannerActivity**

**Purpose**: Capture images and scanned evidence

**Functionality**:
- Initializes CameraX for document/photo capture
- Captures image when user taps shutter button
- Automatically captures GPS location if permission granted
- Saves captured image to evidence folder
- Computes SHA-512 hash immediately
- Returns to CaseDetailActivity with success result

**Camera Features**:
- Auto-focus and exposure
- Flash control
- Preview display
- High-resolution capture
- Image optimization for documents

### **ReportViewerActivity**

**Purpose**: Display and export forensic reports

**Functionality**:
- Loads the generated PDF report
- Displays report content in scrollable view
- Shows all sections: summary, evidence, contradictions, narrative
- Provides export/share functionality
- Allows printing (if available)
- Displays QR code for verification

**Report Sections**:
1. Case header and metadata
2. Evidence index with hashes
3. Forensic narrative
4. B1-B9 Leveler results
5. Contradiction matrix
6. Timeline reconstruction
7. Integrity verification
8. QR code and signatures

### **AudioRecorderActivity**

**Purpose**: Record and capture audio evidence

**Functionality**:
- Initializes audio recorder
- Records audio with quality settings
- Shows waveform visualization
- Saves audio file on stop
- Computes hash and adds to case

### **VideoRecorderActivity**

**Purpose**: Record and capture video evidence

**Functionality**:
- Initializes CameraX video capture
- Records video with timestamp overlay
- Saves video file on stop
- Computes hash and adds to case

---

## ❖ Required App Logic

### Case Creation Flow

```kotlin
1. Generate UUID → caseId = "CASE-XXXXXXXX"
2. Create ForensicCase object with:
   - id: caseId
   - name: user input
   - description: user input
   - createdAt: System.currentTimeMillis()
   - modifiedAt: System.currentTimeMillis()
   - status: CaseStatus.OPEN
3. Save to Room Database
4. Update UI to show new case
```

### Evidence Handling Flow

```kotlin
1. Capture evidence (camera/text/audio/video)
2. Save to app internal storage
3. Calculate SHA-512 hash of content
4. Create ForensicEvidence object:
   - id: "EV-XXXXXXXX"
   - type: EvidenceType enum
   - content: ByteArray
   - contentHash: SHA-512
   - timestamp: capture time
   - location: GPS coordinates (if available)
   - metadata: filename, size, device info
5. Add to case.evidence list
6. Update case modifiedAt timestamp
7. Persist to database
```

### Forensic Processing Flow

```kotlin
1. Load case and all evidence
2. Run LevelerEngine.analyze():
   - B1: Event chronology
   - B2: Contradiction detection
   - B3: Evidence gap analysis
   - B4: Timeline manipulation check
   - B5: Behavioral pattern recognition
   - B6: Financial correlation (if applicable)
   - B7: Communication analysis
   - B8: Jurisdictional compliance
   - B9: Integrity scoring (0-100)
3. Generate narrative with ForensicNarrativeGenerator
4. Compute case integrity hash (SHA-512 of all evidence hashes)
5. Create structured analysis result
6. Return LevelerResult object
```

### Report Generation Flow

```kotlin
1. Seal case (if not already sealed):
   - Seal all unsealed evidence
   - Calculate case integrity hash
   - Set status to SEALED
2. Run forensic analysis
3. Generate narrative text
4. Create evidence summaries
5. Get APK signature hash
6. Generate QR code data:
   - Report ID
   - Case ID
   - Integrity hash
   - APK hash
   - Timestamp
7. Generate PDF with ForensicPdfGenerator:
   - Header with watermark
   - Case metadata
   - Evidence index
   - Narrative
   - Analysis results
   - Hashes and signatures
   - QR code
8. Save PDF to reports folder
9. Set case status to REPORTED
10. Return ForensicReport object
```

### Data Persistence

The app uses Room Database for structured data:

```kotlin
Entities:
- CaseEntity (id, name, description, timestamps, status, integrityHash)
- EvidenceEntity (id, caseId, type, contentHash, metadata, sealed)

DAOs:
- ForensicDao (insert, update, delete, query)

Repository:
- ForensicRepository (abstraction layer)
- Handles coroutines for async operations
- Provides Flow for reactive updates
```

---

## ❖ Technology Stack

### Platform
- **Android Studio** - Hedgehog or later
- **Kotlin** - Primary language
- **AndroidX** - Jetpack components
- **Gradle** - Build system (Kotlin DSL)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **JDK**: Java 17

### Core Libraries
- **Kotlin Coroutines** - For background async operations
- **AndroidX Lifecycle** - ViewModel and LiveData for reactive UI
- **Room Database** - Local SQLite persistence
- **ViewBinding** - Type-safe view access

### Forensic Components
- **CameraX** - Document and photo capture
- **iText7** - PDF report generation (offline-capable)
- **ZXing** - QR code generation
- **AndroidX Security Crypto** - Cryptographic operations (SHA-512, HMAC-SHA512)
- **Google Play Services Location** - GPS location capture

### File I/O
- Internal storage for cases and evidence
- FileProvider for secure file sharing
- JSON for metadata serialization (Gson)

---

## ❖ App Flow Summary

```
┌─────────────────────────────────────────────────────────────┐
│                        [START]                               │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     MainActivity                             │
│  • View existing cases                                       │
│  • Create new case → enter name & description                │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  CaseDetailActivity                          │
│  • View case metadata & evidence list                        │
│  • Add evidence:                                             │
│    - Document (via ScannerActivity)                          │
│    - Photo (via ScannerActivity)                             │
│    - Text note (via dialog)                                  │
│    - Audio (via AudioRecorderActivity)                       │
│    - Video (via VideoRecorderActivity)                       │
│  • Run B1-B9 Leveler analysis                                │
│  • Seal case (lock for edits)                                │
│  • Generate report                                           │
└───────────┬─────────────────────┬───────────────────────────┘
            │                     │
            ▼                     ▼
┌──────────────────┐   ┌──────────────────────────────────────┐
│ ScannerActivity  │   │    ForensicEngine                    │
│ • CameraX        │   │    • Analyze evidence                │
│ • Capture image  │   │    • Detect contradictions           │
│ • GPS location   │   │    • Reconstruct timeline            │
│ • SHA-512 hash   │   │    • Calculate integrity hash        │
│ • Save evidence  │   │    • Seal with HMAC-SHA512           │
└──────┬───────────┘   └─────────────┬────────────────────────┘
       │                             │
       └──────────┬──────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────────┐
│               ReportViewerActivity                           │
│  • Display PDF report                                        │
│  • Show sealed forensic narrative                            │
│  • Evidence index with hashes                                │
│  • B1-B9 analysis results                                    │
│  • QR code for verification                                  │
│  • Export/share report                                       │
└─────────────────────────────────────────────────────────────┘
```

### Detailed User Journey

1. **User launches app** → MainActivity shows list of cases
2. **User taps "Create Case"** → Dialog appears for case name/description
3. **User enters details** → App generates CASE-XXXXXXXX ID
4. **User taps on case** → Navigate to CaseDetailActivity
5. **User taps "Add Document"** → ScannerActivity opens with camera
6. **User captures image** → Image saved with GPS location and hash
7. **User repeats** for multiple evidence items
8. **User taps "Run Analysis"** → B1-B9 Leveler processes evidence
9. **User reviews results** → Dialog shows contradictions, scores, patterns
10. **User taps "Seal Case"** → All evidence locked with HMAC-SHA512
11. **User taps "Generate Report"** → PDF report created
12. **User views report** → ReportViewerActivity displays PDF
13. **User exports report** → PDF shared via Android share sheet

---

## ❖ Data Storage Structure

The app stores data in the following structure:

### Internal Database (Room)
```
ForensicDatabase
├── cases_table
│   ├── id (CASE-XXXXXXXX)
│   ├── name
│   ├── description
│   ├── created_at
│   ├── modified_at
│   ├── status (OPEN/SEALED/REPORTED/ARCHIVED)
│   └── integrity_hash
│
└── evidence_table
    ├── id (EV-XXXXXXXX)
    ├── case_id (foreign key)
    ├── type (DOCUMENT/PHOTO/TEXT/AUDIO/VIDEO)
    ├── content_hash (SHA-512)
    ├── timestamp
    ├── location_json
    ├── metadata_json
    ├── sealed (boolean)
    └── seal_hash (HMAC-SHA512)
```

### File Storage
```
/data/data/org.verumomnis.forensic/files/
├── evidence/
│   ├── CASE-XXXXXXXX/
│   │   ├── EV-YYYYYYYY.jpg
│   │   ├── EV-ZZZZZZZZ.txt
│   │   └── ...
│   └── ...
│
└── reports/
    ├── RPT-AAAAAAAA.pdf
    └── ...
```

### External Storage (Exports)
```
/storage/emulated/0/Android/data/org.verumomnis.forensic/files/
└── exports/
    ├── CASE-XXXXXXXX.zip
    └── ...
```

---

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

1. **Create a Case** - Start by creating a new forensic case with a descriptive name
2. **Add Evidence** - Use the scanner to capture documents, photos, or text notes
3. **Seal Evidence** - Each piece of evidence is cryptographically sealed with SHA-512
4. **Generate Report** - Create a forensic PDF report with full evidence chain
5. **View/Share Reports** - Access and share sealed forensic reports

### Evidence Types

- 📄 Documents (scanned)
- 📷 Photos (captured)
- 📝 Text (notes and observations)
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
├── pdf/                     # PDF report generation
│   └── ForensicPdfGenerator.kt
├── report/                  # Narrative generation
│   └── ForensicNarrativeGenerator.kt
└── ui/                      # User interface
    ├── MainActivity.kt
    ├── ScannerActivity.kt
    ├── CaseDetailActivity.kt
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

## 📜 License

Copyright © 2024 Verum Global Foundation

## 👤 Creator

**Liam Highcock**

---

*AI FORENSICS FOR TRUTH*
