# Verum Omnis Forensic Engine

![Verum Omnis Logo](docs/images/main-logo.png)

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

- **Java**: JDK 17 or later
- **Android SDK**: API Level 34 (Android 14)
- **Android Studio**: Hedgehog (2023.1.1) or later (recommended for development)

### Quick Start

1. Clone the repository:
   ```bash
   git clone https://github.com/Liamhigh/Liam-Highcock.git
   cd Liam-Highcock
   ```

2. Create `local.properties` with your Android SDK path:
   ```bash
   echo "sdk.dir=/path/to/your/android/sdk" > local.properties
   # Example paths:
   # macOS: sdk.dir=/Users/yourusername/Library/Android/sdk
   # Linux: sdk.dir=/home/yourusername/Android/Sdk
   # Windows: sdk.dir=C\:\\Users\\yourusername\\AppData\\Local\\Android\\Sdk
   ```

3. Build the APK:
   ```bash
   # Linux/macOS
   ./gradlew assembleDebug
   
   # Windows
   gradlew.bat assembleDebug
   ```

### Build Commands

| Command | Description | Output |
|---------|-------------|--------|
| `./gradlew assembleDebug` | Build debug APK | `app/build/outputs/apk/debug/app-debug.apk` |
| `./gradlew assembleRelease` | Build release APK (unsigned) | `app/build/outputs/apk/release/app-release-unsigned.apk` |
| `./gradlew testDebugUnitTest` | Run unit tests | Test reports in `app/build/reports/tests/` |
| `./gradlew clean` | Clean build artifacts | - |

### CI/CD

GitHub Actions automatically builds the APK on:
- Push to `main` or `develop` branches
- Pull requests to `main` branch

Build artifacts are available for download from the Actions tab.

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
