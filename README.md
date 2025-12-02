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
